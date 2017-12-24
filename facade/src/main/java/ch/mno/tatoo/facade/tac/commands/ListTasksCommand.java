package ch.mno.tatoo.facade.tac.commands;

import ch.mno.tatoo.facade.tac.data.JobTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * List the tasks defined in the jobcoductor
 * Created by dutoitc on 12/08/15.
 */
public class ListTasksCommand extends AbstractCommand<List<JobTask>> {

    private List<JobTask> jobTasks;


    public ListTasksCommand() {
        super("listTasks");
    }


    @Override
    public List<JobTask> getData() {
        return jobTasks;
    }

    @Override
    public void keepResults(JSONObject result) {
        JSONArray results = (JSONArray) result.get("result");
        jobTasks = new ArrayList<>(results.size());
        for (int i = 0; i < results.size(); i++) {
            JSONObject row = (JSONObject) results.get(i);
            jobTasks.add(JobTask.build(row));
        }
        //RESULT: {"executionTime":{"millis":169,"seconds":0},"result":[
        // {"active":"true","addStatisticsCodeEnabled":"false","applicationBundleName":null,"applicationFeatureURL":null,"applicationGroup":null
        // ,"applicationId":"_NcM8sBEOEeWHTaRHf6GafA","applicationName":"DEADLOCK_TransfertEntrepriseReferentielVersNetSubJob","applicationType":"JOB",
        // "applicationVersion":"3.0","applyContextToChildren":"false","awaitingExecutions":"0","branch":"trunk","commandLineVersion":"5.6.1.20141207_1530",
        // "concurrentExecution":"false","contextName":"Default","description":null,"errorStatus":"NO_ERROR","execStatisticsEnabled":"false",
        // "executionServerId":"JOB_SERVER_1","frozenExecutions":"0","id":"622","idQuartzJob":"65","jobServerLabelHost":"xxserv1",
        // "jobscriptarchivefilename":"/ccv/data/project-ds/Talend-5.6.1/tac/archive/logs/task_65/1434978192059_task_65.zip","label":"DEADLOCK","lastDeploymentDate":null,
        // "lastEndedRunDate":null,"lastRunDate":null,"lastScriptGenerationDate":"2015-06-22 15:00:08.839","lastTaskTraceError":null,"nextFireDate":null,
        // "onUnknownStateJob":"WAIT","onlineStatus":null,"originType":"ZIP","pid":null,"processingState":"false","projectId":"21","projectName":"Tatoo",
        // "regenerateJobOnChange":"false","remaingTimeForNextFire":null,"repositoryName":null,"status":"READY_TO_SEND","svnConnectionAvailable":null,"svnRevision":null,
        // "timeOut":null,"triggersStatus":"NO_TRIGGER","virtualServerLabel":null}


    }

    public List<JobTask> getJobTasks() {
        return jobTasks;
    }


    private String safeString(Object value) {
        if (value == null) return "";
        return value.toString();
    }

    @Override
    public String toString() {
        return "ListTasksCommand[]";
    }


}
