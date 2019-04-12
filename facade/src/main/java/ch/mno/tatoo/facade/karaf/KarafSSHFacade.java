package ch.mno.tatoo.facade.karaf;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.mno.tatoo.facade.karaf.commands.AbstractCommand;
import ch.mno.tatoo.facade.karaf.commands.AbstractKarafAction;
import ch.mno.tatoo.facade.karaf.commands.GenericCommand;
import ch.mno.tatoo.facade.karaf.data.KarafElement;
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
public class KarafSSHFacade implements KarafFacade {

//	private KarafSSHTransport transport;
	public Logger LOG = LoggerFactory.getLogger(KarafFacade.class);
	private Supplier<KarafSSHTransport> transportBuilder;


	public KarafSSHFacade(final String hostname,final  int port, final String user, final String password) {
		transportBuilder = () -> new KarafSSHTransport(hostname, port, user, password);
//		transport = transportBuilder.get();
	}


	public String findData() throws Exception {
		GenericCommand cmd = new GenericCommand("list");
		String list = execute(cmd);

		return list;
	}


	public String findFeatures() throws Exception {
		GenericCommand cmd = new GenericCommand("feature:list");
		String list = execute(cmd);

		return list;
	}

	/**
	 *
	 * @param filename file:/tmp/bundle.jar, mvn:...
	 * @return bundleId
	 */
	public int install(String filename, boolean start) {
		StringBuilder sb = new StringBuilder();
		sb.append("bundle:install ");
		if (start) {
			sb.append("--start ");
		}
		sb.append(filename);
		GenericCommand cmd = new GenericCommand(sb.toString());
		try {
			String ret = execute(cmd);
			Matcher matcher = Pattern.compile("^Bundle ID: (\\d+)").matcher(ret);
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
			throw new RuntimeException("Cannot parse command return: " + ret + " for command " + ret);
		} catch (Exception e) {
			throw new RuntimeException("Cannot execute " + sb.toString() + ": " + e.getMessage(), e);
		}
	}


	public String diag(String bundleName) {
		GenericCommand cmd = new GenericCommand("bundle:diag \"" + bundleName + "\"");
		try {
			return execute(cmd);
		} catch (Exception e) {
			throw new RuntimeException("Cannot execute " + cmd.getCommand() + ": " + e.getMessage(), e);
		}
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


	public void clean(boolean dryRun, String name) throws Exception {
		// Check if feature exists. If it is, remove it
		GenericCommand command = new GenericCommand("list " + name);
		System.out.println("cleaning " + name + "1/2");
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

		// Second chance, exact match
		command = new GenericCommand("list \"" + name + "\"");
		System.out.println("cleaning " + name + "2/2");
		execute(command);
		if (command.getResult().contains(name)) {
			if (dryRun) {
				LOG.info("... DRY-RUN: karaf://uninstall \"" + name + "\"");
				LOG.info("... DRY-RUN: karaf://repo-remove \"" + name + "\"");
			} else {
				execute(new GenericCommand("uninstall \"" + name + "\""));
				execute(new GenericCommand("repo-remove \"" + name + "\""));
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

	public void execute(AbstractKarafAction action) throws Exception  {
		action.execute(transportBuilder);
	}

	public String execute(AbstractCommand command) throws Exception {
		try (KarafSSHTransport transport = transportBuilder.get()){
			String res = transport.execute(command.getCommand());
			command.feed(res);
			return res;
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


}
