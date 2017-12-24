package ch.mno.tatoo.facade.commandline.telnet;

/**
 * Time out on any operation.
 * Created by dutoitc on 11/03/15.
 */
public class TimeoutException extends RuntimeException {
	public TimeoutException(String s) {
		super(s);
	}
}
