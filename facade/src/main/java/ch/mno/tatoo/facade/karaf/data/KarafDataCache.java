package ch.mno.tatoo.facade.karaf.data;

/**
 * Cached Karaf Data
 * Created by dutoitc on 20/08/15.
 */
public class KarafDataCache {

	private String featureUrlList;
	private String featureList;
	private String osgiList;

	public KarafDataCache(String featureUrlList, String featureList, String osgiList) {
		this.featureUrlList = featureUrlList;
		this.featureList = featureList;
		this.osgiList = osgiList;
	}

	public String getFeatureUrlList() {
		return featureUrlList;
	}

	public String getFeatureList() {
		return featureList;
	}

	public String getOsgiList() {
		return osgiList;
	}

}
