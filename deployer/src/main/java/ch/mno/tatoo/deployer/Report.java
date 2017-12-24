package ch.mno.tatoo.deployer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dutoitc on 18/08/15.
 */
public class Report {

    private List<String> servicesSaved = new ArrayList<>();
    private List<String> routesSaved = new ArrayList<>();
    private List<String> jobsSaved = new ArrayList<>();
    private List<String> tasksDeleted = new ArrayList<>();
    private List<String> karafCleaned = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public void addServiceSaved(String name) {
        servicesSaved.add(name);
    }

    public void addRouteSaved(String name) {
        routesSaved.add(name);
    }

    public void addJobSaved(String name) {
        jobsSaved.add(name);
    }

    public void addEsbTaskDeleted(String label) {
        tasksDeleted.add(label);
    }

    public void addKarafClean(String location) {
        karafCleaned.add(location);
    }

    public List<String> getServicesSaved() {
        return servicesSaved;
    }

    public List<String> getRoutesSaved() {
        return routesSaved;
    }

    public List<String> getJobsSaved() {
        return jobsSaved;
    }

    public List<String> getTasksDeleted() {
        return tasksDeleted;
    }

    public List<String> getKarafCleaned() {
        return karafCleaned;
    }

    public String toString() {
        return tasksDeleted.size() + " tasks deleted, " + karafCleaned.size() + " karaf bundle cleaned, " +
                servicesSaved.size() + " services saved, " + routesSaved.size() + " routes saved, " + jobsSaved + " jobs saved.";
    }

    public void addInstallError(String s) {
        errors.add(s);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void write(PrintStream out) {
        if (!errors.isEmpty()) {
            out.println("Erreurs: ");
            errors.forEach(e->out.println("   - " + e));
            out.println();
        }
    }

}
