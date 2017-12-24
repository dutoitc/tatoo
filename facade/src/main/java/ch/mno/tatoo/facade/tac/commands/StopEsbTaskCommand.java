package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * <pre>
 *
 ----------------------------------------------------------
 Command: stopEsbTask
 ----------------------------------------------------------
 Description             : Stop the execution of a given esb task
 Requires authentication : true
 Since                   : 6.1
 Sample                  :
 {
 "actionName": "stopEsbTask",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "taskId": 1
 }
 *
 * </pre>
 */
public class StopEsbTaskCommand extends AbstractCommand<Void> {

    private String taskId;

    /**
     * @param taskId
     * @return
     */
    public static StopEsbTaskCommand buildService(String taskId) {
        return new StopEsbTaskCommand(taskId);
    }

    public StopEsbTaskCommand(String taskId) {
        super("stopEsbTask");
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
        return "StopEsbTask";
    }


    @Override
    public String toString() {
        return "StopEsbTask[" + taskId + "]";
    }

}