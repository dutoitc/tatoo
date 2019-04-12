package ch.mno.tatoo.facade.bonita;

/**
 * Created by dutoitc on 30/04/18.
 */
public class BonitaProcess {

    private Long id;
    private Long processId;
    private String name;
    private String version;
    private String activationState;

    public BonitaProcess(String name, String version, String activationState, Long id, Long processId) {
        this.name = name;
        this.version = version;
        this.activationState = activationState;
        this.id = id;
        this.processId = processId;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getActivationState() {
        return activationState;
    }

    public Long getId() {
        return id;
    }

    public Long getProcessId() { return processId; }

    @Override
    public String toString() {
        return "BonitaProcess[" + name + ' ' + version  + ' ' + activationState + ']';
    }
}
