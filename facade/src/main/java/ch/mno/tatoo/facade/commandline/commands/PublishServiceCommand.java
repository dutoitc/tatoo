package ch.mno.tatoo.facade.commandline.commands;

/**
 * Command object for "publishService"
 * Created by dutoitc on 11/08/15.
 */
public class PublishServiceCommand extends AbstractPublishCommand {


	@Override
	public String getVerb() {
		return "publishService";
	}

	@Override
	public String toString() {
		return "PublishService[" + deployableComponent + " " + deployableComponentVersion + "]";
	}

}
