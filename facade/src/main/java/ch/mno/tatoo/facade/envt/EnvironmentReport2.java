package ch.mno.tatoo.facade.envt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dutoitc on 04/03/16.
 */
public class EnvironmentReport2 {

    public static class Status {
        enum ObjectType {JOB, ROUTE, SERVICE, INSTANCE}

        private ObjectType type;
        private String error;
        private String stacktrace;

        public Status(ObjectType type, String error, String stackTrace) {
            this.type = type;
            this.error = error;
            this.stacktrace = stackTrace;
        }
    }

    private List<Status> errors = new ArrayList<>();
    private List<String> details = new ArrayList<>();

    public List<Status> getErrors() {
        return errors;
    }

    public void setErrors(List<Status> errors) {
        this.errors = errors;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void reportJobErrors(String jobName, String errorStatus, String errorStackTrace) {
        errors.add(new Status(Status.ObjectType.JOB, errorStatus, errorStackTrace));
    }

    public void reportRouteErrors(String featureName, String errorStatus, String errorStackTrace) {
        errors.add(new Status(Status.ObjectType.ROUTE, errorStatus, errorStackTrace));
    }

    public void reportServiceErrors(String featureName, String errorStatus, String errorStackTrace) {
        errors.add(new Status(Status.ObjectType.SERVICE, errorStatus, errorStackTrace));
    }

    public void reportServiceMissingWADL8040(String featureName) {
        errors.add(new Status(Status.ObjectType.SERVICE, featureName + " is missing WADL8040", null));
    }

    public void reportServiceMissingWADL8048(String featureName) {
        errors.add(new Status(Status.ObjectType.SERVICE, featureName + " is missing WADL8048", null));
    }

    public void reportServiceMissingWSDL(String featureName) {
        errors.add(new Status(Status.ObjectType.SERVICE, featureName + " is missing WSDL", null));
    }

    public void reportDetail(String s) {
        details.add(s);
    }

    public void reportInstanceError(String instance, String message) {
        errors.add(new Status(Status.ObjectType.INSTANCE,message, null));
    }

}
