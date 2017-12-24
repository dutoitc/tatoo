package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * <pre>
 *
 ----------------------------------------------------------
 Command: requestUndeployEsbTask
 ----------------------------------------------------------
 Description             : Undeploy the job linked to the esb task given in parameter.
 Requires authentication : true
 Since                   : 6.1
 Sample                  :
 {
 "actionName": "requestUndeployEsbTask",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "taskId": 1
 }
 * </pre>
 */
public class RequestUndeployEsbTaskCommand extends AbstractCommand<Void> {

    private String taskId;


    public RequestUndeployEsbTaskCommand(String taskId) {
        super("requestUndeployEsbTask");
        this.taskId = taskId;
    }

    @Override
    public Void getData() {
        return null;
    }


    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("taskId", taskId);
    }

    @Override
    public void keepResults(JSONObject result) {
        System.out.println(result.toJSONString());
    }

    @Override
    public String getStringResult() {
        return "requestUndeployEsbTask";
    }


    @Override
    public String toString() {
        return "requestUndeployEsbTask[" + taskId + "]";
    }

}