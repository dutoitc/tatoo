package ch.mno.tatoo.facade.commandline.data;

import ch.mno.tatoo.facade.commandline.commands.AbstractCommand;
import org.apache.commons.lang.StringUtils;

/**
 * An exception raised when a command execution has failed
 * Created by dutoitc on 11/08/15.
 */
public class CommandFailedException extends RuntimeException {

	private AbstractCommand command;
	private String log;

	public CommandFailedException(AbstractCommand command, String log) {
		super("Command "+ command.getClass().getName()+" failed: " + StringUtils.abbreviate(log, 200));
		this.command = command;
		this.log = log;
	}

	public AbstractCommand getCommand() {
		return command;
	}

	public String getLog() {
		return log;
	}

}
