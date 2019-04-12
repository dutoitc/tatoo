package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 *
 */
public class PauseTaskCommand extends AbstractCommand<Void> {

	private String taskId;



	public PauseTaskCommand(String taskId) {
		super("pauseTask");
		this.taskId = taskId;
	}

	@Override
	public Void getData() {
		return null;
	}


	@Override
	public void keepResults(JSONObject result) {
		String returnCode = result.get("returnCode").toString();
		if (!returnCode.equals("0")) {
			System.err.println("ReturnCode != 0: " + returnCode);
		}

	}

	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		obj.put("taskId",taskId);
	}

	@Override
	public String toString() {
		return "PauseTaskCommand[]";
	}


}
