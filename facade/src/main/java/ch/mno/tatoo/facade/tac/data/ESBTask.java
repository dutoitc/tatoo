package ch.mno.tatoo.facade.tac.data;

import org.apache.commons.lang.StringUtils;

/**
 * Created by dutoitc on 14/08/15.
 */
public class ESBTask {

	private int id;
	private String applicationVersion;
	private String jobServerLabelHost;
	private String applicationFeatureURL;
	private String applicationName;
	private String pid;
	private String label;
	private FEATURE_TYPE applicationType;
	private String repositoryName;
	private String contextName;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getJobServerLabelHost() {
		return jobServerLabelHost;
	}

	public void setJobServerLabelHost(String jobServerLabelHost) {
		this.jobServerLabelHost = jobServerLabelHost;
	}

	public String getApplicationFeatureURL() {
		return applicationFeatureURL;
	}

	public void setApplicationFeatureURL(String applicationFeatureURL) {
		this.applicationFeatureURL = applicationFeatureURL;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FEATURE_TYPE getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(FEATURE_TYPE applicationType) {
		this.applicationType = applicationType;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	private String str(Object obj, int l) {
		if (obj==null) obj="";
		String s = obj.toString();
		return StringUtils.center(s, l);
	}

	public String toTableString() {
		StringBuilder sb = new StringBuilder();
		sb.append(str(id,4)).append("   ");
		sb.append(str(applicationName, 30)).append("   ");
		sb.append(str(applicationVersion, 15)).append("   ");
		sb.append(str(label, 30)).append("   ");
		sb.append(str(applicationType, 6)).append("   ");
		return sb.toString();
	}
}
