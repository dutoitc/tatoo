package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * Delete a new execution task in TAC
 * Created by dutoitc on 12/08/15.
 */
public class DeleteTaskCommand extends AbstractCommand<Void> {

	private int taskId;
	private boolean deleteAllRelatedData;

	public DeleteTaskCommand(int taskId, boolean deleteAllRelatedData) {
		super("deleteTask");
		this.taskId = taskId;
		this.deleteAllRelatedData = deleteAllRelatedData;
	}

	@Override
	public Void getData() {
		return null;
	}

	@Override
	public void keepResults(JSONObject result) {
		System.out.println("DeleteTaskCommand Result: " + result);
	}

	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		obj.put("taskId", "" + taskId);
		obj.put("deleteAllRelatedData",deleteAllRelatedData);
	}

	@Override
	public String buildJSON(String tacUsername, String tacPassword, String tacEmail) {
		return "{" +
				"  \"actionName\": \"deleteTask\"," +
				"  \"authPass\": \""+tacPassword+"\"," +
				"  \"authUser\": \""+tacUsername+"\"," +
				"  \"deleteAllRelatedData\": true," +
				"  \"taskId\": \""+taskId+"\"" +
				"}";
	}

	@Override
	public String toString() {
		return "DeleteTaskCommand[" + taskId + "]";
	}

}