package ch.mno.tatoo.facade.commandline.commands;

/**
 * Command object for "publishJob"
 * Created by dutoitc on 11/08/15.
 */
public class PublishJobCommand extends AbstractPublishCommand {

	private boolean osgi;


	public void setOsgi(boolean osgi) {
		this.osgi = osgi;
	}


	@Override
	public String getVerb() {
		return "publishJob";
	}

	@Override
	protected void addSpecificParameters(StringBuilder sb) {
		sb.append(" --type ").append(osgi ? "osgi" : "standalone");
	}

	@Override
	public String toString() {
		return "PublishJob[" + deployableComponent + " " + deployableComponentVersion + "]";
	}

}
