package ch.mno.tatoo.deployer;

import ch.mno.tatoo.facade.nexus.NexusEntry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dutoitc on 20/08/15.
 */
public class SourceFilterHelper {


	private static Logger LOG = LoggerFactory.getLogger(SourceFilterHelper.class);


	public static void filterSource(final List<String> includesList, final List<String> excludesList, final String publishedVersion, List<NexusEntry> entries) {
		CollectionUtils.filter(entries, new Predicate<NexusEntry>() {
			@Override
			public boolean evaluate(NexusEntry nexusEntry) {
				if (!nexusEntry.getRelease().startsWith(publishedVersion)) {
					LOG.debug("Filtered out by version " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
					return false;
				}
				if (nexusEntry.getGroupId().contains("service") && !nexusEntry.getArtifactId().endsWith("-feature")) {
					LOG.debug("Filtered out by service/feature " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
					return false;
				}
				if (nexusEntry.getGroupId().contains("route") && !nexusEntry.getArtifactId().endsWith("-feature")) {
					LOG.debug("Filtered out by route/feature " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
					return false;
				}
				if (nexusEntry.getGroupId().contains("job") && nexusEntry.getArtifactId().endsWith("-feature")) {
					LOG.debug("Filtered out by job/feature " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
					return false;
				}


				// Includes: if present, artifactId must match any include
				if (includesList.size() > 0) {
					boolean includes = false;
					for (String patInclude : includesList) {
						if (nexusEntry.getArtifactId().matches(patInclude)) {
							includes = true;
							break;
						}
					}
					if (!includes) {
						LOG.debug("Filtered out by includes " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
						return false; // Should match any include
					}
				}

				// Excludes: if present, any artifactMatch will excludes nexusEntry
				if (excludesList.size() > 0) {
					for (String patExclude : excludesList) {
						if (nexusEntry.getArtifactId().matches(patExclude)) {
							LOG.debug("Filtered out by excludes " + nexusEntry.getArtifactId() + ":" + nexusEntry.getRelease());
							return false;
						}
					}
				}

				return true;
			}
		});
	}

}
