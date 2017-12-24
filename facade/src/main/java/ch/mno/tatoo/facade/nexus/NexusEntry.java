package ch.mno.tatoo.facade.nexus;

/**
 * Object holding Nexus entry data.
 * Created by dutoitc on 18/08/15.
 */
public class NexusEntry {

	private String groupId;
	private String artifactId;
	private String release;
	private String metadataURL;

	public NexusEntry(String groupId, String artifactId, String release, String metadataURL) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.release = release;
		this.metadataURL = metadataURL;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getRelease() {
		return release;
	}


	public String getMetadataURL() {
		return metadataURL;
	}

	@Override
	public String toString() {
		return "NexusEntry{" +
				"groupId='" + groupId + '\'' +
				", artifactId='" + artifactId + '\'' +
				", release='" + release + '\'' +
				'}';
	}
}
