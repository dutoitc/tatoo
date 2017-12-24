package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * Created by dutoitc on 12/08/15.
 * c.f. https://help.talend.com/reader/oYf9gKhmYrkWCiSua4qLeg/SLiAyHyDTjuznLR_F~MiQQ
 */
public abstract class AbstractCommand<O> {

	private String actionName;

	public AbstractCommand(String actionName) {
		this.actionName = actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public abstract O getData();


	public abstract void keepResults(JSONObject result);

	public String buildJSON(String tacUsername, String tacPassword, String tacEmail) {
		// Build JSON request
		JSONObject obj=new JSONObject();
		obj.put("actionName", actionName);
		obj.put("authPass",tacPassword);
		obj.put("authUser",tacUsername);
//		obj.put("userLogin",tacEmail); // TODO: plus demandé par Talend 6 ?
		completeObject(obj);
		return obj.toJSONString();
	}

	public void completeObject(JSONObject obj) {
	}

	public String getStringResult(){
		return "";
	};

	@Override
	public String toString() {
		return "AbstractCommand[]";
	}
}
