package ch.mno.tatoo.facade.envt;

import org.apache.commons.lang.StringUtils;

/**
 * Created by dutoitc on 04/03/16.
 */
public class EnvironmentReport {


    private StringBuffer sbInfo = new StringBuffer();
    private StringBuffer sbErrors = new StringBuffer();


    public String toString() {
        if (sbErrors.length() == 0) {
            return "OK";
        } else {
            return "KO:\n" + sbErrors.toString();
        }
    }


    public void reportJobErrors(String jobName, String errorStatus, String errorStackTrace) {
        sbErrors.append("Job ").append(jobName).append(" has errors: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        sbErrors.append('\n');
    }

    public void reportRouteErrors(String featureName, String errorStatus, String errorStackTrace) {
        sbErrors.append("Route ").append(featureName).append(" has errors: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        sbErrors.append('\n');
    }

    public void reportServiceErrors(String featureName, String errorStatus, String errorStackTrace) {
        sbErrors.append("Service ").append(featureName).append(" has errors: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        sbErrors.append('\n');
    }

    public void reportServiceMissingWADL8040(String featureName) {
        sbErrors.append("Service ").append(featureName).append(" is missing WADL8040\n");
    }

    public void reportServiceMissingWADL8048(String featureName) {
        sbErrors.append("Service ").append(featureName).append(" is missing WADL8048\n");
    }

    public void reportServiceMissingWSDL(String featureName) {
        sbErrors.append("Service ").append(featureName).append(" is missing WSDL\n");
    }

    public void reportDetail(String s) {
        sbInfo.append(s);
    }

    public void reportInstanceError(String instance, String message) {
        sbErrors.append("Instance ").append(instance).append(' ').append(message).append('\n');
    }

    public String getErrors() {
        if (sbErrors.length()==0) {
            return "No errors";
        }
        return sbErrors.toString();
    }

    public String getStatus() {
        if (sbErrors.length()==0) {
            return "OK";
        }
        return "KO: " + sbErrors.toString();
    }

    public void reportError(String s) {
        sbErrors.append(s).append('\n');
    }
}
