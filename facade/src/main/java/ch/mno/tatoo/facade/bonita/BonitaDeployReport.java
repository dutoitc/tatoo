package ch.mno.tatoo.facade.bonita;

import java.util.LinkedHashSet;
import java.util.Set;

public class BonitaDeployReport {

    private Set<BonitaProcess> bpmDeployed = new LinkedHashSet<>();
    private Set<String> errors = new LinkedHashSet<>();

    public void addBarDeployed(BonitaProcess bonitaProcess) {
        bpmDeployed.add(bonitaProcess);
    }

    public void addError(String message) {
        errors.add(message);
    }

    public Set<BonitaProcess> getBpmDeployed() {
        return bpmDeployed;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public long getNbBPMDeployed() {
        return bpmDeployed.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Deployment report: \n");
        sb.append("  - ").append(bpmDeployed).append(" BPM deployed\n");
        if (hasErrors()) {
            sb.append(" - Errors: \n");
            errors.forEach(e-> sb.append("   - ").append(e).append('\n'));
        }
        return sb.toString();
    }
}
