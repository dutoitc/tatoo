package ch.mno.tatoo.facade.karaf.commands;

import ch.mno.tatoo.common.reporters.ConsoleReporter;
import ch.mno.tatoo.common.reporters.Reporter;
import ch.mno.tatoo.facade.karaf.KarafSSHTransport;

import java.util.function.Supplier;

/**
 * Created by dutoitc on 12/08/15.
 */
public abstract class AbstractKarafAction {

	protected int maxResponseTimeMSec=1000; // Wait time for first byte
	protected int maxInactivityTimeMSec=300; // When the response started, wait for max ... inactivity on stdout
	private boolean dryMode;
	protected Reporter reporter;


	public AbstractKarafAction() {
		this(new ConsoleReporter());
	}

	public AbstractKarafAction(Reporter reporter) {
		this.reporter = reporter;
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

	public abstract void execute(Supplier<KarafSSHTransport> transportBuilder) throws Exception;
}
