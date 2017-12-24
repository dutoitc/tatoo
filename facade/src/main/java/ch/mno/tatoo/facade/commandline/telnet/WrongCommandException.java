package ch.mno.tatoo.facade.commandline.telnet;

/**
 * Wrong command-line command
 * Created by dutoitc on 11/03/15.
 */
public class WrongCommandException extends RuntimeException {
	public WrongCommandException(String s) {
		super(s);
	}
}
