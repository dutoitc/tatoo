package ch.mno.tatoo.facade.tac.commands;

import ch.mno.tatoo.facade.tac.data.ESBTask;
import ch.mno.tatoo.facade.tac.data.FEATURE_TYPE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * List the tasks defined in the jobcoductor
 * Created by dutoitc on 12/08/15.
 */
public class ListESBTasksCommand extends AbstractCommand<List<ESBTask>> {

    private List<ESBTask> jobTasks;


    public ListESBTasksCommand() {
        super("listEsbTasks");
    }


    @Override
    public List<ESBTask> getData() {
        return jobTasks;
    }

    @Override
    public void keepResults(JSONObject result) {
        JSONArray results = (JSONArray) result.get("result");
        jobTasks = new ArrayList<>(results.size());
        for (int i = 0; i < results.size(); i++) {
            JSONObject row = (JSONObject) results.get(i);
            ESBTask data = new ESBTask();
            data.setId(Integer.parseInt(row.get("id").toString()));
            data.setApplicationVersion(safeString(row.get("applicationVersion")));
            data.setJobServerLabelHost(safeString(row.get("jobServerLabelHost")));
            data.setApplicationFeatureURL(safeString(row.get("applicationFeatureURL")));
            data.setApplicationName(safeString(row.get("applicationName")));
            data.setPid(safeString(row.get("pid")));
            data.setLabel(safeString(row.get("label")));
            data.setApplicationType(FEATURE_TYPE.valueOf(row.get("applicationType").toString()));
            data.setRepositoryName(safeString(row.get("repositoryName")));
            data.setContextName(safeString(row.get("contextName")));
            jobTasks.add(data);
        }

    }

    public List<ESBTask> getJobTasks() {
        return jobTasks;
    }


    private String safeString(Object value) {
        if (value == null) return "";
        return value.toString();
    }


    @Override
    public String toString() {
        return "ListESBTasksCommand[]";
    }


}
