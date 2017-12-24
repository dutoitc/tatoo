package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * <pre>
 *
 ----------------------------------------------------------
 Command: requestDeployEsbTask
 ----------------------------------------------------------
 Description             : Deploy the job linked to the esb task given in parameter.
 Requires authentication : true
 Since                   : 6.1
 Sample                  :
 {
 "actionName": "requestDeployEsbTask",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "taskId": 1
 }
 * }
 * </pre>
 */
public class RequestDeployEsbTaskCommand extends AbstractCommand<Void> {

    private String taskId;

    /**
     * @param taskId
     * @return
     */
    public static RequestDeployEsbTaskCommand buildService(String taskId) {
        return new RequestDeployEsbTaskCommand(taskId);
    }

    public RequestDeployEsbTaskCommand(String taskId) {
        super("requestDeployEsbTask");
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
        return "RequestDeployEsbTask";
    }


    @Override
    public String toString() {
        return "RequestDeployEsbTaskCommand[" + taskId + "]";
    }

}