package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by dutoitc on 12/08/15.
 */
public class GenericCommand extends AbstractCommand {


	public GenericCommand(String command) {
		super(command);
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public void keepResults(JSONObject result) {
//		System.out.println(result);

		if (result.get("result")!=null) {
			JSONArray arr = (JSONArray) result.get("result");
			for (int i=0; i<arr.size(); i++) {
				JSONObject row = (JSONObject) arr.get(i);
				System.out.println(row.get("applicationType")+"\t"+row.get("applicationName")+"\t" + row.get("applicationVersion")+"\t"+row.get("STATUS"));
			}
		}

	}

	@Override
	public String toString() {
		return "GenericCommand[]";
	}


}
