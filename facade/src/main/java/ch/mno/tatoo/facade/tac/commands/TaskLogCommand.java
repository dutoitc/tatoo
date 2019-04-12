package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * @See GetTaskExecutionStatus
 * @See GetTaskIdByName
 */
public class TaskLogCommand extends AbstractCommand<String> {

    private long taskId;
    private String log;

    public static TaskLogCommand build(long taskId) {
        return new TaskLogCommand(taskId);
    }

    public TaskLogCommand(long taskId) {
        super("taskLog");
        this.taskId = taskId;
    }

    @Override
    public String getData() {
        return log;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("taskId", taskId);
        obj.put("lastExecution", true);
    }

    @Override
    public void keepResults(JSONObject result) {
        log = result.get("result").toString();
    }

    @Override
    public String getStringResult() {
        return "Reading task #" + taskId + " log ";
    }


    @Override
    public String toString() {
        return "TaskLogCommand[" + taskId + "]";
    }

}