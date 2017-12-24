package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 *
 ----------------------------------------------------------
 Command: createTrigger
 ----------------------------------------------------------
 Description             : Create a trigger.
 - 'taskId' : it could be the task id, exec plan id, esb publisher id
 - 'timezoneOption': must be one of those values [JOBSERVER_TIMEZONE, TAC_TIMEZONE, CUSTOM_TIMEZONE]
 - if this option is 'CUSTOM_TIMEZONE', the field 'timezone' becomes mandatory.
 - For "timezone" property, refer to http://joda-time.sourceforge.net/timezones.html for more details
 on the syntax
 - 'pauseOnError' : if set to true, pause the trigger when the task fail.
 Requires authentication : true
 Since                   : 5.6
 Sample                  :
 {"Samples": [
 {
 "actionName": "createTrigger",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "daysOfMonth": "10",
 "description": "This is a cron trigger",
 "hours": "5,10",
 "label": "CronTrigger1",
 "minutes": "1,10",
 "months": "*",
 "pauseOnError": true,
 "taskId": 1,
 "timezoneOption": "JOBSERVER_TIMEZONE",
 "triggerType": "CronTrigger",
 "years": "2016"
 }
 ]}
 */
public class CreateCronTriggerCommand extends AbstractCommand<Void> {

	private String taskId="*";
	private String label="*";
	private String description="";
	private String minutes="*";
	private String hours="*";
	private String daysOfMonth="*";
	private String daysOfWeek="*";
	private String months="*";
	//dayOfWeek
	private String years="*";


	public static CreateCronTriggerCommand build(String label, String taskId) {
		return new CreateCronTriggerCommand(label, taskId);
	}

	public CreateCronTriggerCommand(String label, String taskId) {
		super("createTrigger");
		this.label = label;
		this.taskId = taskId;
	}


	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		if ("*".equals(daysOfWeek)) {
			obj.put("daysOfMonth", daysOfMonth);
		} else if  ("*".equals(daysOfMonth)) {
			obj.put("daysOfWeek", daysOfWeek);
		} else {
			throw new RuntimeException("daysOfMonth et daysOfWeek sont exclusifs.");
		}


		obj.put("description", description);
		obj.put("hours", hours);
		obj.put("label", label);
		obj.put("minutes", minutes);
		obj.put("months", months);
		obj.put("pauseOnError", "false");
		obj.put("taskId", taskId);
		obj.put("timezoneOption", "TAC_TIMEZONE");
		obj.put("triggerType", "CronTrigger");
		obj.put("years", years);
	}

	@Override
	public Void getData() {
		return null;
	}

	@Override
	public void keepResults(JSONObject result) {
		Long returnCode = (Long) result.get("returnCode");
		if (returnCode==null || returnCode!=0) {
			throw new RuntimeException("Résultat!=0 pour CreateCrontTrigger");
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public void setDaysOfMonth(String daysOfMonth) {
		this.daysOfMonth = daysOfMonth;
	}

	public void setMonths(String months) {
		this.months = months;
	}

	public void setYears(String years) {
		this.years = years;
	}

	public void setDaysOfWeek(String daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	@Override
	public String toString() {
		return "createTrigger["+label+","+taskId+"]";
	}


}