package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * Creation a task execution plan.
 *
 Metaservlet documentation:
 <pre>
 ----------------------------------------------------------
 Command: createTask
 ----------------------------------------------------------
 Description             : Add a new execution task in TAC and return the task ID.
 - projectName and jobName: the project and job must exist.
 - onUnknownStateJob: could be [WAIT, KILL_TASK, RESTART_TASK, RECOVER_TASK]
 - contextName: "Default" is the default value.
 - 'pauseOnError' : if set to true, pause all the triggers on the task when the task fail.
 Requires authentication : true
 Since                   : 5.0
 Sample                  :
 {
 "actionName": "createTask",
 "active": true,
 "applyContextToChildren": false,
 "authPass": "admin",
 "authUser": "admin@company.com",
 "branch": "trunk",
 "contextName": "Default",
 "description": "task1 for extracting data from DB1",
 "execStatisticsEnabled": false,
 "executionServerName": "xxserv1",
 "jobName": "job1",
 "jobVersion": "1.0",
 "onUnknownStateJob": "WAIT",
 "pauseOnError": false,
 "projectName": "tproject1",
 "regenerateJobOnChange": false,
 "taskName": "task1",
 "timeout": 3600
 }
 </pre>
 */
public class CreateTaskCommand extends AbstractCommand<Void> {

    private String projectName;
    private String jobName;
	private String jobVersion;
    private final String serverLabel;

	public CreateTaskCommand(String projectName, String jobName, String jobVersion, String serverLabel) {
		super("createTask");
        this.projectName = projectName;
		this.jobName = jobName;
		this.jobVersion = jobVersion;
        this.serverLabel = serverLabel;
	}



	@Override
	public void completeObject(JSONObject obj) {
     super.completeObject(obj);
     obj.put("active", true);
     obj.put("applyContextToChildren", false);
     obj.put("branch", "trunk");
     obj.put("contextName", "Default");
     obj.put("description", jobName);
     obj.put("execStatisticsEnabled", false);
     obj.put("executionServerName", serverLabel);
     obj.put("jobName", jobName);
     obj.put("jobVersion", jobVersion);
     obj.put("onUnknownStateJob", "WAIT");
     obj.put("pauseOnError", false);
     obj.put("projectName", projectName);
     obj.put("regenerateJobOnChange", false);
     obj.put("taskName", "jobName");
     obj.put("timeout", 3600);
    }
    @Override
    public Void getData() {
        return null;
    }

    @Override
	public void keepResults(JSONObject result) {
System.out.println(result.toJSONString());
	}


    @Override
    public String toString() {
        return "CreateTaskCommand["+jobName+" "+jobVersion+"]";
    }

}