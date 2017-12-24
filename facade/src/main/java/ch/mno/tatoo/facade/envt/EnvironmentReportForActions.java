package ch.mno.tatoo.facade.envt;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Report with errors in french, asking for actions
 * Created by dutoitc on 04/03/16.
 */
public class EnvironmentReportForActions extends EnvironmentReport {

    List<String> infos = new ArrayList<>();
    List<String> errors = new ArrayList<>();



    public void reportJobErrors(String jobName, String errorStatus, String errorStackTrace) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Job ").append(jobName).append(" a des erreurs: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        errors.add(sbErrors.toString());
    }

    public void reportRouteErrors(String featureName, String errorStatus, String errorStackTrace) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Route ").append(featureName).append(" a des erreurs: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        errors.add(sbErrors.toString());
    }

    public void reportServiceErrors(String featureName, String errorStatus, String errorStackTrace) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Service ").append(featureName).append(" a des erreurs: ").append(errorStatus).append("; ");
        if (errorStackTrace!=null) {
            sbErrors.append(StringUtils.abbreviate(errorStackTrace.replace("\n", ";"), 150));
        }
        errors.add(sbErrors.toString());
    }

    public void reportServiceMissingWADL8040(String featureName) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Redémarrer la route ").append(featureName.replace("-feature", "")).append(" [WADL8040 manquant]");
        errors.add(sbErrors.toString());
    }

    public void reportServiceMissingWADL8048(String featureName) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Redémarrer le service ").append(featureName.replace("-feature", "")).append(" [WADL8048 manquant]");
        errors.add(sbErrors.toString());
    }

    public void reportServiceMissingWSDL(String featureName) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Redémarrer le service ").append(featureName.replace("-feature", "")).append(" [WSDL manquant]");
        errors.add(sbErrors.toString());
    }

    public void reportDetail(String s) {
        infos.add(s);
    }

    public void reportInstanceError(String instance, String message) {
        StringBuilder sbErrors = new StringBuilder();
        sbErrors.append("Instance ").append(instance).append(' ').append(message);
        errors.add(sbErrors.toString());
    }

    public List<String> getInfos() {
        return infos;
    }


    public List<String> getErrorsList() {
        return errors;
    }


    public String getStatus() {
        if (errors.isEmpty()) {
            return "OK";
        }
        return "KO:" + String.join(",", errors);
    }


    public String toString() {
        return getStatus();
    }


}
