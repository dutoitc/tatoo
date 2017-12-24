package ch.mno.tatoo.builder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by dutoitc on 11/08/15.
 */
public class Context {


	public enum PROPERTIES {
		COMMANDLINE_SERVER_URL, COMMANDLINE_SERVER_PORT, TAC_SERVER_URL, TAC_SERVER_USERNAME, TAC_SERVER_PASSWORD, PROJECT, SVN_URL, SVN_USERNAME, SVN_PASSWORD,
		SOURCE_PATTERN, SOURCE_BLACKLIST,
		NEXUS_URL, NEXUS_USERNAME, APPLICATION_GROUPID, NEXUS_PASSWORD
	}


	Properties values = new Properties();

	public Context(String filename) throws IOException {
		FileInputStream is = new FileInputStream(filename);
		values.load(is);
		System.out.println("Loaded " + values.size() + " properties from " + filename + ".");
		validate();
	}

	public String get(PROPERTIES property) {
		return values.getProperty(property.name());
	}


	/**
	 * Validate that all keys exists in property file
	 */
	private void validate() {
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

	public String getCommandlineServerURL() {
		return get(PROPERTIES.COMMANDLINE_SERVER_URL);
	}

	public int getCommandlineServerPort() {
		return Integer.parseInt(get(PROPERTIES.COMMANDLINE_SERVER_PORT));
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

	public String getProject() {
		return get(PROPERTIES.PROJECT);
	}

	public String getSvnURL() {
		return get(PROPERTIES.SVN_URL);
	}

	public String getSvnUsername() {
		return get(PROPERTIES.SVN_USERNAME);
	}

	public String getSvnPassword() {
		return get(PROPERTIES.SVN_PASSWORD);
	}

	public String getSourcePattern() {
		return get(PROPERTIES.SOURCE_PATTERN);
	}

	public List<String> getSourceBlacklist() {
		return Arrays.asList(get(PROPERTIES.SOURCE_BLACKLIST).split(","));
	}

	public String getNexusURL() {
		return get(PROPERTIES.NEXUS_URL);
	}

	public String getNexusUsername() {
		return get(PROPERTIES.NEXUS_USERNAME);
	}

	public String getNexusPassword() {
		return get(PROPERTIES.NEXUS_PASSWORD);
	}

	public boolean isBlacklisted(String jobFullName) {
		List<String> blacklist = getSourceBlacklist();

		for (String black: blacklist) {
			if (jobFullName.indexOf(black)>=0) {
				System.out.println(jobFullName + " blacklisted by " + black);
				return true;
			}
		}
		return false;
	}
}
