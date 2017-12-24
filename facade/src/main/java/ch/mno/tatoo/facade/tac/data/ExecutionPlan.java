package ch.mno.tatoo.facade.tac.data;

import java.util.List;
import java.util.Set;

/**
 * Created by dutoitc on 28/11/17.
 */
public class ExecutionPlan {

    private String execPlanTimeOut;
    // planParts
    private String idQuartzJob;
    private Long planId;
    private String label;
    private String planPrms;
    private String status;
    private List<String> jobs;

    public String getExecPlanTimeOut() {
        return execPlanTimeOut;
    }

    public void setExecPlanTimeOut(String execPlanTimeOut) {
        this.execPlanTimeOut = execPlanTimeOut;
    }

    public String getIdQuartzJob() {
        return idQuartzJob;
    }

    public void setIdQuartzJob(String idQuartzJob) {
        this.idQuartzJob = idQuartzJob;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlanPrms() {
        return planPrms;
    }

    public void setPlanPrms(String planPrms) {
        this.planPrms = planPrms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
    }

    public List<String> getJobs() {
        return jobs;
    }
}
