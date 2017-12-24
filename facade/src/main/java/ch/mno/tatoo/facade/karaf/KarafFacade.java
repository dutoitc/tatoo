package ch.mno.tatoo.facade.karaf;

import ch.mno.tatoo.facade.karaf.commands.AbstractCommand;
import ch.mno.tatoo.facade.karaf.commands.AbstractKarafAction;
import ch.mno.tatoo.facade.karaf.commands.GenericCommand;
import ch.mno.tatoo.facade.karaf.data.KarafElement;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 24/10/17.
 */
// FIXME: Karaf close the session after some time, or session goes timeout. Yet force reconnection for each command to workaround this.
public class KarafFacade implements AutoCloseable {

//	private KarafSSHTransport transport;
	public Logger LOG = LoggerFactory.getLogger(KarafFacade.class);
	private Supplier<KarafSSHTransport> transportBuilder;

//	private final Pattern PAT_OSGILIST=Pattern.compile("\\[.*\\] (.*?) \\((.*?)\\)");
//	private final Pattern PAT_FEATURELIST=Pattern.compile("\\[.*?\\] \\[(.*?) .*?\\] (.*?) ");
//	private final Pattern PAT_FEATUREURL=Pattern.compile("(mvn:http:.*?\\/xml)");

	public KarafFacade(final String hostname,final  int port, final String user, final String password) {
		transportBuilder = () -> new KarafSSHTransport(hostname, port, user, password);
//		transport = transportBuilder.get();
	}


	public String findData() throws Exception {
		GenericCommand cmd = new GenericCommand("list");
		String list = execute(cmd);

		return list;
	}


	/**
	 *
	 * @return list of maven references, eg. ch.mno.dummyapp.job.E_Achat.A_Commun/NormaliserAdresseReferentiel-control-bundle/17.23.0-SNAPSHOT
	 * @throws IOException
     */
	public List<KarafElement> findKarafElement() throws Exception {
		GenericCommand cmd = new GenericCommand("list -l");
		String list = execute(cmd);
		List<KarafElement> result = new ArrayList<>();

		for (String line: list.split("\n")) {
			String[] spl = line.split("│");
			if (spl.length==5) {
				KarafElement el = new KarafElement();
				el.setId(spl[0].trim());
				el.setState(spl[1].trim());
				el.setVersion(spl[3].trim());
				el.setLocation(spl[4].trim());
				result.add(el);
			}
		}
		return result;
	}
//
//
//	public void findKarafFeatures() {
//		GenericCommand cmd = new GenericCommand("feature:list -l");
//		String list = execute(cmd);
//		List<KarafElement> result = new ArrayList<>();
//
//		for (String line: list.split("\n")) {
//			String[] spl = line.split("│");
//			if (spl.length==5) {
//				KarafElement el = new KarafElement();
//				el.setId(spl[0].trim());
//				el.setState(spl[1].trim());
//				el.setVersion(spl[3].trim());
//				el.setLocation(spl[4].trim());
//				result.add(el);
//			}
//		}
//		return result;
//	}


	public void clean(boolean dryRun, String name) throws Exception {
		// Check if feature exists. If it is, remove it
		GenericCommand command = new GenericCommand("list " + name);
		execute(command);
		if (command.getResult().contains(name)) {
			if (dryRun) {
				LOG.info("... DRY-RUN: karaf://uninstall "+name);
				LOG.info("... DRY-RUN: karaf://repo-remove "+name);
			} else {
				execute(new GenericCommand("uninstall " + name));
				execute(new GenericCommand("repo-remove " + name));
			}
		}
	}
	/**
	 *
	 * @return  job: v1,v2,v3\r\njob:... or null if none found
	 * @throws IOException
	 */
	public String findDoubleVersions() throws Exception {
		String data = findData();
		//[7966] [Active     ] [Created     ] [       ] [   80] mvn:ch.mno.dummyapp.service.E_Achat.E_Bitcoin.A_RegistreCommerce/IdentificationNumeroIdeBitcoinHROperation/17.12.0

//		System.out.println(data.getOsgiList());

		Set<String> blacklist = new HashSet<>();
		blacklist.add("Apache");
		blacklist.add("Talend");
		blacklist.add("Objenesis");
		blacklist.add("cloning");
		blacklist.add("camel");
		blacklist.add("Commons");
		blacklist.add("JAX-RS");
		blacklist.add("ZooKeeper");
		blacklist.add("Scala");
		blacklist.add("null");


		// Find versions
		Map<String, List<String>> found = new HashMap<>();
		//Pattern pattern = Pattern.compile("mvn:ch.mno.dummyapp.*/(.*)/(\\d+\\.\\d+\\.\\d+)");
		// [1285] [Active     ] [Created     ] [Started] [   80] REE_ReceptionAnnonces (17.13.1)
		Pattern pattern = Pattern.compile("] ([a-zA-Z0-9_\\.]+) \\((\\d+\\.\\d+\\.\\d+)\\)");
		Matcher matcher = pattern.matcher(data);
		while (matcher.find()) {
			String bundle = matcher.group(1);
			String version = matcher.group(2);
//			System.out.println("Checking "+ bundle +" "+version);

			if (blacklist.contains(bundle.split(" ")[0])) continue;

//			System.out.println(bundle + "  " + version);
			if (found.containsKey(bundle)) {
				found.get(bundle).add(version);
			} else {
				List<String> lst = new ArrayList<>();
				lst.add(version);
				found.put(bundle, lst);
			}
		}

		// List duplicates
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, List<String>> entry: found.entrySet()) {
			if (entry.getValue().size()>1) {
				sb.append(entry.getKey()).append(": ").append(StringUtils.join(entry.getValue(), ",")).append("\r\n");
			}
		}
		if (sb.length()>0) {
			return sb.toString();
		}
		return null;

	}

	/**
	 * Clean Karaf features
	 * @param dryRun
	 * @param names (without -feature)
	 * @throws IOException
     */
//	public void clean(boolean dryRun, List<String> names) throws Exception {
//		try (KarafSSHTransport transport = transportBuilder.get()){
//			for (String name : names) {
//				clean(dryRun, name);
//				clean(dryRun, name + "Operation");
//				clean(dryRun, name + "-control-bundle");
//			}
//		}
//	}

	public void execute(AbstractKarafAction action) throws Exception  {
		action.execute(transportBuilder);
	}

	public String execute(AbstractCommand command) throws Exception {
		try (KarafSSHTransport transport = transportBuilder.get()){
			String res = transport.execute(command.getCommand());
			command.feed(res);
			return res;
//		} catch (IllegalStateException e) {
//			if (e.getMessage().contains("session is being closed")) {
//				LOG.info("Session to Karaf is closed, reopening it");
//				transport = transportBuilder.get();
//				String res = transport.execute(command.getCommand());
//				command.feed(res);
//				return res;
//			} else {
//				throw e;
//			}
		}
	}


	@Override
	public void close() throws Exception {
		try {
//			transport.close();
		} catch (Exception e) {
			// Do nothing
		}
	}


	/** Clean les bundle, features, repo-list correspondant à cette version */
	/*public void cleanVersion(String version) throws Exception {
		if (!Pattern.compile("\\d\\d\\.\\d\\d").matcher(version).matches()) {
			throw new RuntimeException("Mauvaise version: 'dd.dd' attendu, mais "+version+" obtenu");
		}
		version+=".";

		// Désinstaller les bundles
		Arrays.asList(execute(new GenericCommand("bundle:list | grep "+version+" | grep -v Uninstalled")).split("\n")).stream()
				.map(f -> f.split("│"))
				.filter(f -> f.length == 5)
				.map(f -> f[0])
				.peek(f -> System.out.println("Uninstalling " + f))
				.forEach(f -> {
					try {
						// TODO: continuer à cleaner
						execute(new GenericCommand("bundle:uninstall " + f));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

		// Désinstaller les features
		Arrays.asList(execute(new GenericCommand("feature:list | grep "+version+" | grep -v Uninstalled")).split("\n")).stream()
				.map(f -> f.split("│"))
				.filter(f -> f.length == 5)
				.map(f -> f[0])
				.peek(f -> System.out.println("Uninstalling " + f))
				.forEach(f -> {
					try {
						// TODO: continuer à cleaner
						execute(new GenericCommand("feature:uninstall " + f));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

		// Supprimer les repo-list
		Arrays.asList(execute(new GenericCommand("feature:repo-list | grep "+version)).split("\n")).stream()
				.map(f -> f.split("│"))
				.filter(f -> f.length == 2)
				.map(f -> f[0])
				.peek(f -> System.out.println("Uninstalling " + f))
				.forEach(f -> {
					try {
						// TODO: continuer à cleaner
						execute(new GenericCommand("feature:repo-remove " + f));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

	}
*/

	public static void main(String[] args) throws Exception {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
		loggerList.stream().forEach(tmpLogger -> tmpLogger.setLevel(Level.INFO));



		KarafFacade facade = new KarafFacade("hostname", 8101, "karaf", "karaf");



		// Clean Karaf by version  !! la version peut être présente dans des bundles non-project !!
//		if (true) {
//			facade.cleanVersion("18.11");
//		}


		/*Arrays.asList(facade.execute(new GenericCommand("bundle:list | grep 17.23")).split("\n")).stream()
				.map(f->f.split("│"))
				.filter(f->f.length==5)
				.map(f->f[0])
				.peek(f->System.out.println("Uninstalling " + f))
				.forEach(f -> {
					try {
						// TODO: continuer à cleaner
						facade.execute(new GenericCommand("bundle:uninstall " + f.replace("-feature", "")));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

		Arrays.asList(facade.execute(new GenericCommand("feature:repo-list | grep 17.23")).split("\n")).stream()
			.map(f->f.split("│"))
			.filter(f->f.length==2)
				.map(f->f[0])
				.peek(f->System.out.println("Uninstalling " + f))
				.forEach(f -> {
					try {
						// TODO: continuer à cleaner
						facade.execute(new GenericCommand("feature:repo-remove " + f.replace("-feature", "")));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});*/


		//System.out.println(cache.getFeatureList());

		//facade.clean(false, Arrays.asList("INFRA_Route_Ping"));
//		facade.findKarafElement().forEach(System.out::println);
//
//        tacFacade.getESBTasks().forEach(t -> {
//            try {
//                cleanAction.execute(t, report);
//            } catch (ActionException e) {
//                LOG.error("Erreur au cleanup: " + e.getMessage(), e);
//            }
//        });
//
//        facade.execute(new )
//        System.out.println(facade.execute(new GenericCommand("feature:list")));
	}


}
