package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * @See GetTaskExecutionStatus
 */
public class RunTaskCommand extends AbstractCommand<Void> {

    private long taskId;
    private String execRequestId;
    private String returnCode;

    public static RunTaskCommand build(long taskId) {
        return new RunTaskCommand(taskId);
    }

    public RunTaskCommand(long taskId) {
        super("runTask");
        this.taskId = taskId;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("taskId", String.valueOf(taskId));
    }

    @Override
    public Void getData() {
        return null;
    }

    @Override
    public void keepResults(JSONObject result) {
        //{ execRequestId: "1432855205979_a5zn8", executionTime: { millis: 564, seconds: 0 }, returnCode: 0 }
        execRequestId = result.get("execRequestId").toString();
        returnCode = result.get("returnCode").toString();
    }

    @Override
    public String getStringResult() {
        return "Running task " + taskId;
    }

    public String getExecRequestId() {
        return execRequestId;
    }

    public String getReturnCode() {
        return returnCode;
    }


    @Override
    public String toString() {
        return "RunTaskCommand[" + taskId + "]";
    }

}