package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * <pre>
 *
 * ----------------------------------------------------------
 * Command: requestDeploy
 * ----------------------------------------------------------
 * Description             : Deploy the job linked to the task given in parameter.
 * Requires authentication : true
 * Since                   : 5.0
 * Sample                  :
 * {
 * "actionName": "requestDeploy",
 * "authPass": "admin",
 * "authUser": "admin@company.com",
 * "taskId": 1
 * }
 * Specific error codes    :
 * 170: Error while deploying the task
 * }
 * </pre>
 */
public class RequestDeployTaskCommand extends AbstractCommand<Void> {

    private String taskId;

    /**
     * @param taskId
     * @return
     */
    public static RequestDeployTaskCommand buildService(String taskId) {
        return new RequestDeployTaskCommand(taskId);
    }

    public RequestDeployTaskCommand(String taskId) {
        super("requestDeploy");
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
        return "RequestDeploy";
    }


    @Override
    public String toString() {
        return "RequestDeployTaskCommand[" + taskId + "]";
    }

}