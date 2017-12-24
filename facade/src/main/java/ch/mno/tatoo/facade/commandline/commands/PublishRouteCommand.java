package ch.mno.tatoo.facade.commandline.commands;

/**
 * Command object for "publishRoute"
 * Created by dutoitc on 11/08/15.
 */
public class PublishRouteCommand extends AbstractPublishCommand {

	@Override
	public String getVerb() {
		return "publishRoute";
	}

	@Override
	public String toString() {
		return "PublishRoute[" + deployableComponent + " " + deployableComponentVersion + "]";
	}

}
