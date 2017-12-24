package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * @See GetTaskExecutionStatus
 * @See GetTaskIdByName
 */
public class GetTaskIdByNameCommand extends AbstractCommand {

	private String name;

	public static GetTaskIdByNameCommand build(String name) {
		return new GetTaskIdByNameCommand(name);
	}

	public GetTaskIdByNameCommand(String name) {
		super("getTaskIdByName");
		this.name = name;
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		obj.put("name", name); // TODO: check this
	}

	@Override
	public void keepResults(JSONObject result) {
		// TODO
	}

	@Override
	public String toString() {
		return "GetTaskIdByNameCommand[" + name + "]";
	}

}