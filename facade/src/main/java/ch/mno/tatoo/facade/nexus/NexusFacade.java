package ch.mno.tatoo.facade.nexus;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


	public static List<NexusEntry> findNexusEntries(String url) throws IOException, URISyntaxException, InterruptedException {
		NexusFacade facade = new NexusFacade();
		List<String> metadata = facade.findMetadata(url);
		List<NexusEntry> entries = facade.findNexusEntries(metadata);
		return entries;
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

	/**
	 * Check if the given version is already used by a binary file on Nexus.
	 * @param nexusURL
	 * @param version
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static boolean isVersionUsed(String nexusURL, String version) throws InterruptedException, IOException, URISyntaxException {
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