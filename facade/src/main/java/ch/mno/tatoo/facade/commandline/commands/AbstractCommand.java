package ch.mno.tatoo.facade.commandline.commands;

/**
 * Created by dutoitc on 11/08/15.
 */
public abstract class AbstractCommand {

	private String lastData;

	public abstract String build();

	public void setLastData(String lastData) {
		this.lastData = lastData.replace("talend>", "").replace("\r\n\r\n","").replace("\n\n", "\n");
	}

	public String getLastData() {
		return lastData;
	}

	public boolean isImmediate() {
		return false; // true if command write response immediately (list* write response without wait, synchronously
	}

	@Override
	public String toString() {
		return "Command[]";
	}

}
