package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * @See GetTaskExecutionStatus
 * @See GetTaskIdByName
 */
public class GetTaskExecutionStatusCommand extends AbstractCommand<Void> {

    private String execRequestId;

    public static GetTaskExecutionStatusCommand build(String execRequestId) {
        return new GetTaskExecutionStatusCommand(execRequestId);
    }

    public GetTaskExecutionStatusCommand(String execRequestId) {
        super("getTaskExecutionStatus");
        this.execRequestId = execRequestId;
    }

    @Override
    public Void getData() {
        return null;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("execRequestId", execRequestId);
    }

    @Override
    public void keepResults(JSONObject result) {
        // TODO
    }

    @Override
    public String getStringResult() {
        return "Reading task execution #" + execRequestId + " status ";
    }


    @Override
    public String toString() {
        return "GetTaskExecutionStatusCommand[" + execRequestId + "]";
    }


}