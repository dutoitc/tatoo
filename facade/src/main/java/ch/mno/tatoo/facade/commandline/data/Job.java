package ch.mno.tatoo.facade.commandline.data;

/**
 * Object holding Job component data
 * Created by dutoitc on 01/04/15.
 */
public class Job extends AbstractDeployableComponent {

	public Job(String jobName, String path) {
		super(jobName, path);
	}

	public Service convertToService() {
		Service service = new Service(name, path);
		service.setVersion(getVersion());
		return service;
	}

}
