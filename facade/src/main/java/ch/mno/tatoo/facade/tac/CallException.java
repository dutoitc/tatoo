package ch.mno.tatoo.facade.tac;

import ch.mno.tatoo.facade.tac.commands.AbstractCommand;

/**
 * Exception raised when a call failed on the server, with an error code.
 * Created by dutoitc on 14/08/15.
 */
public class CallException extends Exception {

	private AbstractCommand command;

	public CallException(String message, AbstractCommand command) {
		super(message);
		this.command = command;
	}

	public AbstractCommand getCommand() {
		return command;
	}

}
