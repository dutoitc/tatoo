package ch.mno.tatoo.facade.karaf.commands;


/**
 * Created by dutoitc on 12/08/15.
 */
@Deprecated // Use AbstractKarafAction
public abstract class AbstractCommand {

	private String command;
	private String buffer;
	protected int maxResponseTimeMSec=1000; // Wait time for first byte
	protected int maxInactivityTimeMSec=300; // When the response started, wait for max ... inactivity on stdout
	private boolean dryMode;

	public AbstractCommand(String command) {
		this.command = command;
	}

	public AbstractCommand(String command, int maxResponseTimeMSec, int maxInactivityTimeMSec) {
		this.command = command;
		this.maxResponseTimeMSec = maxResponseTimeMSec;
		this.maxInactivityTimeMSec = maxInactivityTimeMSec;
	}


	public String getCommand() {
		return command;
	}

	public String getResult() {
		return buffer;
	}

	public void feed(String res) {
		this.buffer = res;
	}

	public int getMaxResponseTimeMSec() {
		return maxResponseTimeMSec;
	}

	public int getMaxInactivityTimeMSec() {
		return maxInactivityTimeMSec;
	}

	public boolean isDryMode() {
		return dryMode;
	}

	public void setDryMode(boolean dryMode) {
		this.dryMode = dryMode;
	}
}
