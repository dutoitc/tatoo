package ch.mno.tatoo.facade.nexus;

import ch.mno.tatoo.facade.common.FacadeException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Facade to ease operations on on Nexus.
 * Created by dutoitc on 18/08/15.
 */
public class NexusFacade {

	private static final Logger LOG = LoggerFactory.getLogger(NexusFacade.class);

	private static final Pattern PAT_GROUPID=Pattern.compile("groupId>(.*?)<");
	private static final Pattern PAT_ARTIFACTID=Pattern.compile("artifactId>(.*?)<");
	//	private static final Pattern PAT_RELEASE=Pattern.compile("release>(.*?)<");
	private static final Pattern PAT_VERSION=Pattern.compile("version>(.*?)<");


	public static List<NexusEntry> findNexusEntries(String url) throws FacadeException {
		try {
			NexusFacade facade = new NexusFacade();
			List<String> metadata = facade.findMetadata(url);
			List<NexusEntry> entries = facade.findNexusEntries(metadata);
			return entries;

		} catch (Exception e) {
			throw new FacadeException("An error occured: " + e.getMessage(), e);
		}
	}


	public static String findProcessVersion(String nexusPath, NexusEntry entry) throws IOException {
		if (entry.getGroupId().contains("service")) {
			return findProcessVersionService(nexusPath, entry);
		} else if (entry.getGroupId().contains("job")) {
			return findProcessVersionJob(nexusPath, entry);
		} else  if (entry.getGroupId().contains("route")) {
			return findProcessVersionRoute(nexusPath, entry);
		} else {
			throw new RuntimeException("Cannot read process version of " + entry);
		}
	}

	public static String findProcessVersionService(String nexusPath, NexusEntry entry) throws IOException {
		String url = nexusPath + "/"
				+ entry.getGroupId().replace(".","/") +"/"
				+ entry.getArtifactId().replace(".","/") + "/"
				+ entry.getRelease() + "/"
				+ entry.getArtifactId() + "-"
				+ entry.getRelease()+".jar";


		JarInputStream is = new JarInputStream(new URL(url).openStream());
		Attributes mainAttributes = is.getManifest().getMainAttributes();
		String pkg = mainAttributes.getValue("Export-Package");
		String process = pkg.split(";")[0];
		String version = process.substring(mainAttributes.getValue("Bundle-SymbolicName").length()+1);
		return version.replace("_",".");
	}


	public static String findProcessVersionRoute(String nexusPath, NexusEntry entry) throws IOException {
		String url = nexusPath + "/"
				+ entry.getGroupId().replace(".","/") +"/"
				+ entry.getArtifactId().replace(".","/") + "/"
				+ entry.getRelease() + "/"
				+ entry.getArtifactId() + "-"
				+ entry.getRelease()+".jar";


		JarInputStream is = new JarInputStream(new URL(url).openStream());
		Attributes mainAttributes = is.getManifest().getMainAttributes();
		String pkg = mainAttributes.getValue("Export-Package");
		String process = pkg.split(";")[0];
		int p1 = process.lastIndexOf('_');
		int p2 = process.substring(0,p1-1).lastIndexOf('_');
		String version = process.substring(p2+1);
		//String version = process.substring(mainAttributes.getValue("Bundle-SymbolicName").length()+1);
		return version.replace("_",".");
	}


	public static String findProcessVersionJob(String nexusPath, NexusEntry entry) throws IOException {
		String url = nexusPath + "/"
				+ entry.getGroupId().replace(".","/") +"/"
				+ entry.getArtifactId().replace(".","/") + "/"
				+ entry.getRelease() + "/"
				+ entry.getArtifactId() + "-"
				+ entry.getRelease()+".zip";

		/*File zip = File.createTempFile("nexusfacade",".tmp");
		try (FileWriter output = new FileWriter(zip)) {
			IOUtils.copy(new URL(url).openStream(), output);
			output.flush();
		}
		ZipFile zipFile = new ZipFile(zip);
		*/

		ZipInputStream zip = null;
		byte[] buffer = new byte[10000];
		String jobInfo=null;
		try {
			zip = new ZipInputStream(new URL(url).openStream());

			ZipEntry zipEntry;
			do{
				zipEntry = zip.getNextEntry();
			} while(!"jobInfo.properties".equals(zipEntry.getName()));


			while(zip.available()>0 && jobInfo==null) {
				int l = zip.read(buffer, 0, buffer.length);
				jobInfo = new String(buffer, 0, l, "UTF-8");
			}
			zip.closeEntry();
		} finally {
			if (zip != null)
				zip.close();
		}

		if (jobInfo!=null) {
			String version = Arrays.stream(jobInfo.split("\n")).filter(l -> l.startsWith("jobVersion=")).map(l -> l.substring(11)).collect(Collectors.joining());
			return version;
		} else {
			throw new RuntimeException("JobInfo not found in " + url);
		}
	}


	private List<String> findMetadata(String url) throws InterruptedException, IOException, URISyntaxException {
		// Finding maven-metadata.xml
		List<String> metadata = NexusMetadataCrawler.findMetadata(url);
		return metadata;
	}

	private List<NexusEntry> findNexusEntries(List<String> metadata) throws IOException {
		// Extract services and routes
		List<NexusEntry> nexusEntries = new ArrayList<>();
		for (String metadataURL: metadata) {
			String data = IOUtils.toString(new URL(metadataURL));

			Matcher matchGroupId = PAT_GROUPID.matcher(data);
			if (!matchGroupId.find()) continue;
			String groupId = matchGroupId.group(1);

			Matcher matchArtifactId = PAT_ARTIFACTID.matcher(data);
			if (!matchArtifactId.find()) continue;
			String artifactId = matchArtifactId.group(1);

			/*Matcher matchRelease = PAT_RELEASE.matcher(data);
			if (!matchRelease.find()) continue;
			String release = matchRelease.group(1);
			*/

			Map<String, Integer> versions = new HashMap<String, Integer>();
			Matcher matchVersion = PAT_VERSION.matcher(data);
			while (matchVersion.find()) {
				String version = matchVersion.group(1);
				try {
					int p = version.indexOf(".", version.indexOf(".") + 1);
					if (p == -1) {
						// Numéro de version à deux numéros: xx.yy: on ajoute tout
						versions.put(version, -1);
					} else {
						String versionMajorMinor = version.substring(0, p);
						String minor = version.substring(p + 1);
						int myMinor = Integer.parseInt(minor);

						if (!versions.containsKey(versionMajorMinor) || myMinor > versions.get(versionMajorMinor)) {
							versions.put(versionMajorMinor, myMinor);
						}
					}
				} catch (Exception e) {
					System.err.println("Cannot parse version of " + groupId + "   "+artifactId+ "   " + version + ", ignoring.");
				}
			}

			for (Map.Entry<String, Integer>version: versions.entrySet()) {
				String versionStr = version.getKey()+(version.getValue()==-1?"":("."+version.getValue()));
				NexusEntry entry = new NexusEntry(groupId, artifactId, versionStr, metadataURL);
				nexusEntries.add(entry);

			}


		}
		return nexusEntries;
	}

	public static boolean isVersionUsed(String nexusURL, String version) throws FacadeException {
		List<NexusEntry> entries = findNexusEntries(nexusURL);
		for (NexusEntry entry: entries) {
			if (entry.getRelease().equals(version)) {
				LOG.info("Version "+version+" already used: " + entry.getGroupId()+":"+entry.getArtifactId()+":"+entry.getRelease());
				return true;
			}
		}
		return false;
	}


}