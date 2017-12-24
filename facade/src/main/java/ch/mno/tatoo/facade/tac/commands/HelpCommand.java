package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * Created by dutoitc on 12/08/15.
 */
public class HelpCommand extends AbstractCommand<Void> {

	private String commandName;


	public HelpCommand() {
		this("all");
	}

	public HelpCommand(String commandName) {
		super("help");
		this.commandName = commandName;
	}

	@Override
	public Void getData() {
		return null;
	}


	@Override
	public void keepResults(JSONObject result) {
		String helpMessage = result.get("help").toString();
		System.out.println(helpMessage);

	}

	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		obj.put("commandName",commandName);
	}

	@Override
	public String toString() {
		return "HelpCommand[]";
	}


}
