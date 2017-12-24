package ch.mno.tatoo.facade.tac.commands;

import ch.mno.tatoo.facade.tac.data.FEATURE_TYPE;
import org.json.simple.JSONObject;

/**
 * <pre>
 * ----------------------------------------------------------
 * Command: saveESBTask
 * ----------------------------------------------------------
 * Description             : Add a new esb execution task in TAC and return the task ID.
 * - featureType: could be [ROUTE, SERVICE, GENERIC]
 * - runtimeContext: "Default" is the default value.
 * Requires authentication : true
 * Since                   : 5.4
 * Sample                  :
 * {
 *     "actionName"        : "saveESBTask",
 *     "authPass"          : "admin",
 *     "authUser"          : "admin@company.com",
 *     "description"       : "esbTask1's description",
 *     "featureName"       : "DemoRESTConsumer-feature",
 *     "featureType"       : "ROUTE",
 *     "featureUrl"        : "mvn:org.example/DemoRESTConsumer-feature/0.1.0-SNAPSHOT/xml",
 *     "featureVersion"    : "0.1.0-SNAPSHOT",
 *     "repository"        : "repo-snapshot",
 *     "runtimeContext"    : "Default",
 *     "runtimePropertyId" : "DemoRESTConsumer",
 *     "runtimeServerName" : "xxserv1",
 *     "tag"               : "tag1",
 *     "taskName"          : "esbTask1"
 * }
 * </pre>
 */
public class SaveESBTaskCommand extends AbstractCommand<Void> {


    private String featureName;
    private FEATURE_TYPE featureType;
    private String featureUrl;
    private String featureVersion;
    private String runtimePropertyId;
    private String createdId;
    private String serverLabel;

    /**
     * @param featureName
     * @param featureVersion
     * @param servicePath    Studio path, like "G_Technique" or "A_truc.B_bhose"
     * @return
     */
    public static SaveESBTaskCommand buildService(String featureName, String featureVersion, String servicePath, String serverLabel) {
        return new SaveESBTaskCommand(featureName, FEATURE_TYPE.SERVICE,
                "mvn:ch.mno.dummyapp.service." + servicePath + "/" + featureName + "/" + featureVersion + "/xml",
                featureVersion, featureName.replace("-feature", ""), serverLabel);
    }


    public static SaveESBTaskCommand buildJob(String bundleName, String featureVersion, String path, String version, String serverLabel) {
        return new SaveESBTaskCommand(bundleName, FEATURE_TYPE.JOB,
                //"mvn:ch.mno.dummyapp.job."+path+"/"+bundleName+"/"+featureVersion+"/zip",
                null,
                version, bundleName, serverLabel);
    }

    public static SaveESBTaskCommand buildRoute(String featureName, String featureVersion, String servicePath, String serverLabel) {
        return new SaveESBTaskCommand(featureName, FEATURE_TYPE.ROUTE,
                "mvn:ch.mno.dummyapp.route." + servicePath + "/" + featureName + "/" + featureVersion + "/xml",
                featureVersion, featureName.replace("-feature", ""), serverLabel);
    }


    public SaveESBTaskCommand(String featureName, FEATURE_TYPE featureType, String featureUrl, String featureVersion,
                              String runtimePropertyId, String serverLabel) {
        super("saveEsbTask");
        this.featureName = featureName;
        this.featureType = featureType;
        this.featureUrl = featureUrl;
        this.featureVersion = featureVersion;
        this.runtimePropertyId = runtimePropertyId;
        this.serverLabel = serverLabel;
    }


    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("description", "Déploiement automatisé de " + featureName);
        obj.put("featureName", featureName);
        obj.put("featureType", featureType.name());
        obj.put("featureUrl", featureUrl);
        obj.put("featureVersion", featureVersion);
        obj.put("repository", "releases");
        obj.put("runtimeContext", "Default");
        obj.put("runtimePropertyId", runtimePropertyId);
        obj.put("runtimeServerName", serverLabel);
        obj.put("tag", "default"); // New in Talend6: must exist
        obj.put("taskName", featureName.replace("-feature", ""));
    }

    @Override
    public Void getData() {
        return null;
    }

    @Override
    public void keepResults(JSONObject result) {
        Object id1 = result.get("taskId");
        if (id1==null) {
            throw new RuntimeException("Missing id in JSON response: " + result.toJSONString());
        }
        String id = id1.toString();
        createdId = id;
    }

    @Override
    public String getStringResult() {
        return "Created ESB task " + featureName + ":" + featureVersion + " of type " + featureType + " wih id " + createdId;
    }


    @Override
    public String toString() {
        return "SaveESBTaskCommand[" + featureName + " " + featureVersion + "]";
    }

}