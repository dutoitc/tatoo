package ch.mno.tatoo.facade.karaf.data;

/**
 * Karaf Feature
 * Created by dutoitc on 17/08/15.
 */
public class Feature {


	private boolean loaded;
	private String uri;

	public Feature(boolean loaded, String uri) {
		this.loaded = loaded;
		this.uri = uri;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public String getUri() {
		return uri;
	}

}
