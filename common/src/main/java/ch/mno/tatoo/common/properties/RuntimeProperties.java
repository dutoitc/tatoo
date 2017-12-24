package ch.mno.tatoo.common.properties;


import ch.mno.tatoo.common.reporters.Reporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Properties: loaded from a file (and validated) and free properties (set manually but not validated)
 * Created by dutoitc on 11/08/15.
 */
@Deprecated // Must use pure java properties ?
public class RuntimeProperties {


    public enum PROPERTIES {
        TAC_SERVER_URL, TAC_SERVER_USERNAME, TAC_SERVER_PASSWORD, TAC_SERVER_EMAIL, TAC_SERVER_LABEL,
        KARAF_HOSTNAME, KARAF_PORT, KARAF_USERNAME, KARAF_PASSWORD,
        NEXUS_URL,
        DB_URI, DB_USERNAME, DB_PASSWORD,
        WEB_PREFIX, SERVER_DS, SERVER_NET, ENV, TAC_SUFFIX,
        BLACKLIST,
        APPLICATION_GROUPID, DRY_MODE
    }


    java.util.Properties values = new java.util.Properties();
    java.util.Properties valuesFree = new java.util.Properties();

    public RuntimeProperties(String filename, Reporter reporter) throws IOException {
        FileInputStream is = new FileInputStream(filename);
        values.load(is);
        reporter.logTrace("Chargé " + values.size() + " properties depuis " + filename + ".");
    }

    public List<String> getIncludes() {
        String includesStr = get("includes");
        if (includesStr == null) {
            includesStr = ".*";
        }
        return Arrays.asList(includesStr.split(","));
    }

    public List<String> getExcludes() {
        String excludesStr = get("excludes");
        if (excludesStr == null) {
            excludesStr = "31415926535";
        } // Et pi alors ?... on n'exclut rien !
        return Arrays.asList(excludesStr.split(","));
    }


    public boolean isDryRun() {
        return "true".equals(valuesFree.getProperty("dryRun"));
    }

    public void setDryRun(boolean value) {
        valuesFree.put("dryRun", value ? "true" : "false");
    }

    public void put(String key, String value) {
        if (value == null) {
            throw new RuntimeException("Value must not be null for key=" + key);
        }
        valuesFree.put(key, value);
    }

    public String get(PROPERTIES property) {
        return values.getProperty(property.name());
    }

    public int getInt(PROPERTIES property) {
        String value = null;
        try {
            value = values.getProperty(property.name());
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Value is not int: " + property.name() + "=" + value);
        }
    }


    public String get(String key) {
        return (String) valuesFree.get(key);
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

}
