package ch.mno.tatoo.facade.bonita;

import java.util.LinkedHashSet;
import java.util.Set;

public class BonitaUndeployReport {

    private Set<BonitaProcess> bpmUndeployed = new LinkedHashSet<>();
    private Set<String> errors = new LinkedHashSet<>();

    public void addBarUndeployed(BonitaProcess bonitaProcess) {
        bpmUndeployed.add(bonitaProcess);
    }

    public void addError(String message) {
        errors.add(message);
    }

    public Set<BonitaProcess> getBpmUndeployed() {
        return bpmUndeployed;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public long getNbBPMDeployed() {
        return bpmUndeployed.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Undeploy report: \n");
        sb.append("  - ").append(bpmUndeployed).append(" BPM undeployed\n");
        if (hasErrors()) {
            sb.append(" - Errors: \n");
            errors.forEach(e-> sb.append("   - ").append(e).append('\n'));
        }
        return sb.toString();
    }
}
