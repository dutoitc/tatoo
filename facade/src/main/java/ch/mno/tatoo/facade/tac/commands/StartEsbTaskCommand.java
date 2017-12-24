package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * <pre>
 *
 ----------------------------------------------------------
 Command: startEsbTask
 ----------------------------------------------------------
 Description             : Start the execution of a given esb task
 Requires authentication : true
 Since                   : 6.1
 Sample                  :
 {
 "actionName": "startEsbTask",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "taskId": 1
 }
 * }
 * </pre>
 */
public class StartEsbTaskCommand extends AbstractCommand<Void> {

    private String taskId;

    /**
     * @param taskId
     * @return
     */
    public static StartEsbTaskCommand buildService(String taskId) {
        return new StartEsbTaskCommand(taskId);
    }

    public StartEsbTaskCommand(String taskId) {
        super("startEsbTask");
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
        return "StartEsbTask";
    }


    @Override
    public String toString() {
        return "StartEsbTask[" + taskId + "]";
    }

}