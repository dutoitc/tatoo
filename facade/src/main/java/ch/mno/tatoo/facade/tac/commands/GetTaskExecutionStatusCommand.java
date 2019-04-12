package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * @See GetTaskExecutionStatus
 * @See GetTaskIdByName
 */
public class GetTaskExecutionStatusCommand extends AbstractCommand<String> {

    private String execRequestId;
    private String jobExitCode;
    private String returnCode;
    private String execBasicStatus;
    private String execDetailedStatusLabel;
    private String execDetailedStatus;

    public static GetTaskExecutionStatusCommand build(String execRequestId) {
        return new GetTaskExecutionStatusCommand(execRequestId);
    }

    public GetTaskExecutionStatusCommand(String execRequestId) {
        super("getTaskExecutionStatus");
        this.execRequestId = execRequestId;
    }

    @Override
    public String getData() {
        return execDetailedStatus;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("execRequestId", execRequestId);
    }

    @Override
    public void keepResults(JSONObject result) {
        jobExitCode = result.get("jobExitCode").toString();
        returnCode = result.get("returnCode").toString();
        execBasicStatus = result.get("execBasicStatus").toString();
        execDetailedStatusLabel = result.get("execDetailedStatusLabel").toString();
        execDetailedStatus = result.get("execDetailedStatus").toString();
    }

    @Override
    public String getStringResult() {
        return "Reading task execution #" + execRequestId + " status ";
    }


    @Override
    public String toString() {
        return "GetTaskExecutionStatusCommand[" + execRequestId + "]";
    }

    public String getExecutionStatus() {
        return "GetTaskExecutionStatusCommand{" +
                "execRequestId='" + execRequestId + '\'' +
                ", jobExitCode='" + jobExitCode + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", execBasicStatus='" + execBasicStatus + '\'' +
                ", execDetailedStatusLabel='" + execDetailedStatusLabel + '\'' +
                ", execDetailedStatus='" + execDetailedStatus + '\'' +
                '}';
    }


}