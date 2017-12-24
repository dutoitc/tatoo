package ch.mno.tatoo.facade.tac.data;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

/**
 * Created by dutoitc on 14/08/15.
 */
public class JobTask {

	private int id;
	private FEATURE_TYPE featureType;
	private String label;
	private String applicationName;
	private String applicationVersion;
	private String errorStatus;
	private String status;
	private String triggersStatus;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FEATURE_TYPE getFeatureType() {
		return featureType;
	}

	public void setFeatureType(FEATURE_TYPE featureType) {
		this.featureType = featureType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTriggersStatus() {
		return triggersStatus;
	}

	public void setTriggersStatus(String triggersStatus) {
		this.triggersStatus = triggersStatus;
	}


	private String str(Object obj, int l) {
		if (obj==null) obj="";
		String s = obj.toString();
		return StringUtils.center(s, l);
	}
	public String toTableString() {
		StringBuilder sb = new StringBuilder();
		sb.append(str(id,4)).append("   ");
		sb.append(str(featureType, 7)).append("   ");
		sb.append(str(label, 30)).append("   ");
		sb.append(str(applicationName, 30)).append("   ");
		sb.append(str(applicationVersion, 6)).append("   ");
		sb.append(str(errorStatus, 10)).append("   ");
		sb.append(str(status, 10)).append("   ");
		sb.append(str(triggersStatus, 10)).append("   ");
		return sb.toString();
	}

	public static JobTask build(JSONObject row) {
		JobTask data = new JobTask();
		data.setId(Integer.parseInt(row.get("id").toString()));
		data.setFeatureType(FEATURE_TYPE.valueOf(row.get("applicationType").toString()));
		data.setLabel(safeString(row.get("label")));
		data.setApplicationName(safeString(row.get("applicationName")));
		data.setApplicationVersion(safeString(row.get("applicationVersion")));
		data.setErrorStatus(safeString(row.get("errorStatus")));
		data.setStatus(safeString(row.get("status")));
		data.setTriggersStatus(safeString(row.get("triggersStatus")));
		return data;
	}


	private static String safeString(Object value) {
		if (value == null) return "";
		return value.toString();
	}


}
