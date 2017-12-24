package ch.mno.tatoo.facade.commandline.telnet;

/**
 * Command-line FAILED
 * Created by dutoitc on 11/03/15.
 */
public class ExecutionFailedException extends Exception {

	public ExecutionFailedException(String title, String s) {
		super(title, new RuntimeException(s));
	}

}
