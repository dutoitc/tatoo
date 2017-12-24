package ch.mno.tatoo.deployer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by dutoitc on 11/08/15.
 */
public class Context {



	enum PROPERTIES {
		TAC_SERVER_URL, TAC_SERVER_USERNAME, TAC_SERVER_PASSWORD, TAC_SERVER_EMAIL, TAC_SERVER_LABEL,
		KARAF_HOSTNAME, KARAF_PORT, KARAF_USERNAME, KARAF_PASSWORD,
		NEXUS_URL, INCLUDES, EXCLUDES, DRY_RUN
	}


	Properties values = new Properties();

	public Context(String filename) throws IOException {
		FileInputStream is = new FileInputStream(filename);
		values.load(is);
		System.out.println("Loaded " + values.size() + " properties from " + filename + ".");
	}

	public String get(PROPERTIES property) {
		return values.getProperty(property.name());
	}


	/**
	 * Validate that all keys exists in property file
	 */
	public void validate() {
		StringBuilder sb = new StringBuilder();
		for (PROPERTIES key : PROPERTIES.values()) {
			if (!values.containsKey(key.name())) {
				if (sb.length() > 0) sb.append(',');
				sb.append(key);
			}
		}
		if (sb.length() > 0) {
			throw new RuntimeException("Missing properties: " + sb.toString());
		}
	}

	public String getTacURL() {
		return get(PROPERTIES.TAC_SERVER_URL);
	}

	public String getTacUsername() {
		return get(PROPERTIES.TAC_SERVER_USERNAME);
	}

	public String getTacPassword() {
		return get(PROPERTIES.TAC_SERVER_PASSWORD);
	}


	public String getTacServerLabel() {
		String value=get(PROPERTIES.TAC_SERVER_LABEL);
		if (value==null) return "serv1";
		return value;
	}


	public List<String> getIncludes() {
		return Arrays.asList(get(PROPERTIES.INCLUDES).split(","));
	}

	public List<String> getExcludes() {
		return Arrays.asList(get(PROPERTIES.EXCLUDES).split(","));
	}

	public String getNexusURL() {
		return get(PROPERTIES.NEXUS_URL);
	}


	public String getKarafHostname() {
		return get(PROPERTIES.KARAF_HOSTNAME);
	}

	public int getKarafPort() {
		return Integer.parseInt(get(PROPERTIES.KARAF_PORT));
	}

	public String getKarafUsername() {
		return get(PROPERTIES.KARAF_USERNAME);
	}

	public String getKarafPassword() {
		return get(PROPERTIES.KARAF_PASSWORD);
	}

	public boolean isDryRun() {
		return Boolean.parseBoolean(get(PROPERTIES.DRY_RUN));
	}


}
