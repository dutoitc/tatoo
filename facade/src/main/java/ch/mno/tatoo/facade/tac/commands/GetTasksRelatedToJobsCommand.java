package ch.mno.tatoo.facade.tac.commands;

import ch.mno.tatoo.facade.tac.data.JobTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dutoitc on 12/08/15.
 */
public class GetTasksRelatedToJobsCommand extends AbstractCommand<List<JobTask>> {

	private List<JobTask> tasks = new ArrayList<>();

	public GetTasksRelatedToJobsCommand() {
		super("getTasksRelatedToJobs");
	}


	@Override
	public List<JobTask> getData() {
		return tasks;
	}

	@Override
	public void keepResults(JSONObject result) {
		JSONArray root = (JSONArray) result.get("root");
		for (int i=0; i<root.size(); i++) {
			JSONObject row = (JSONObject) root.get(i);
			tasks.add(JobTask.build(row));
		}
	}


	private String safeString(Object value) {
		if (value == null) return "";
		return value.toString();
	}

	public List<JobTask> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "GetTasksRelatedToJobsCommand[]";
	}


}
