package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * Creation a task execution plan.
 * <p>
 * Metaservlet documentation:
 * <pre>
 * ----------------------------------------------------------
 * Command: associatePreGeneratedJob
 * ----------------------------------------------------------
 * Description             : Create a new execution task with a pre-generated zip file
 * - targetConductor: could be [JOBCONDUCTOR, BIGDATA_STREAMING]. "JOBCONDUCTOR" is the default value.
 * - nexusRepository: if create task from Nexus, using parameters:
 * nexusRepository/nexusGroupId/nexusArtifactId/nexusVersion[/nexusJobVersionSuffix].
 * - nexusJobVersionSuffix: [Optional] use it when job in nexus with named like test-1.0.0-20170516.043751-1.zip
 * - taskType: [Optional] Distinguish task type: Normal/Artifact, default: Normal
 * - importType: the position where select the zip file from; 'Nexus' or 'File', default value is 'File'
 * Requires authentication : true
 * Since                   : 5.2
 * Sample                  :
 * {
 * "actionName": "associatePreGeneratedJob",
 * "active": true,
 * "authPass": "admin",
 * "authUser": "admin@company.com",
 * "contextName": "Default",
 * "description": "task1's description",
 * "executionServerName": "serv1",
 * "filePath": "'/home/talend/generatedJob.zip'",
 * "importType": "Nexus",
 * "logLevel": "Info",
 * "nexusArtifactId": "test",
 * "nexusGroupId": "org.example",
 * "nexusJobVersionSuffix": "1.0.0-20170516.043751-1",
 * "nexusRepository": "snapshots",
 * "nexusVersion": "0.1.0-SNAPSHOT",
 * "onUnknownStateJob": "WAIT",
 * "pauseOnError": false,
 * "taskName": "task1",
 * "taskType": "Artifact",
 * "timeout": 3600
 * }
 * Specific error codes    :
 * 180: file is not a valid file or not exist
 * 181: nexus parameters may be not correct
 * </pre>
 */
public class AssociatePreGeneratedJobCommand extends AbstractCommand<Void> {

    private String description;
    private String serverLabel;
    private String taskName;
    private String nexusGroupId;
    private String nexusArtifactId;
    private String nexusVersion;

    public AssociatePreGeneratedJobCommand(String actionName, String description, String serverLabel, String taskName, String nexusGroupId, String nexusArtifactId, String nexusVersion) {
        super("associatePreGeneratedJob");
        this.description = description;
        this.serverLabel = serverLabel;
        this.taskName = taskName;
        this.nexusGroupId = nexusGroupId;
        this.nexusArtifactId = nexusArtifactId;
        this.nexusVersion = nexusVersion;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("active", true);
        obj.put("contextName", "Default");
        obj.put("description", description);
        obj.put("execStatisticsEnabled", false);
        obj.put("executionServerName", serverLabel);
        obj.put("importType", "Nexus");
        obj.put("logLevel", "Info");
        obj.put("nexusArtifactId", nexusArtifactId);
        obj.put("nexusGroupId", nexusGroupId);
        obj.put("nexusRepository", "releases");
        obj.put("nexusVersion", nexusVersion);
        obj.put("onUnknownStateJob", "WAIT");
        obj.put("pauseOnError", false);
        obj.put("taskName", taskName);
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
        return "AssociatePreGeneratedJobCommand[" + nexusGroupId + "." + nexusArtifactId + "]";
    }

}