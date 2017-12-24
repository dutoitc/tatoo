package ch.mno.tatoo.facade.karaf.commands;

/**
 * Created by dutoitc on 17/08/15.
 */
public class GenericCommand extends AbstractCommand {


	public GenericCommand(String command) {
		super(command, 10000, 300);
	}



}
