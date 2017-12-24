package ch.mno.tatoo.facade.commandline.data;

/**
 * Object holding information of what can be deployed: job, route, service
 * Created by dutoitc on 11/08/15.
 */
public abstract class AbstractDeployableComponent {

	/** Component name, like "JOB_BuyBitcoin" */
	protected String name;

	/** Component path, like "F_Bank.C_Crypto" */
	protected String path;

	/** Component version */
	private Float version;

	public AbstractDeployableComponent(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFullName() {
		return path+"."+ name;
	}

	public void setVersion(Float version) {
		this.version = version;
	}

	public Float getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + path + ":" + name + ":" + version;
	}

}
