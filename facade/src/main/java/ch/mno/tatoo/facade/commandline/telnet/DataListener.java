package ch.mno.tatoo.facade.commandline.telnet;

/**
 * Interface to receive data read on server
 */
public interface DataListener {
	void newLine(String line);
}
