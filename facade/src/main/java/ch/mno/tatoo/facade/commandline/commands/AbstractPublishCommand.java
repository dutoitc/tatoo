package ch.mno.tatoo.facade.commandline.commands;

import ch.mno.tatoo.facade.commandline.data.AbstractDeployableComponent;

/**
 * Created by dutoitc on 11/08/15.
 */
public abstract class AbstractPublishCommand<T extends AbstractDeployableComponent> extends AbstractCommand {

	protected AbstractDeployableComponent deployableComponent;
	protected String deployableComponentVersion;
	private String publishedVersion;
	private boolean snapshot;
	private String artifactRepository;
	private String nexusUsername;
	private String nexusPassword;
	private String groupId;

	/** example: ch.mno.project.service */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setDeployableComponent(AbstractDeployableComponent deployableComponent) {
		this.deployableComponent = deployableComponent;
	}

	public void setDeployableComponentVersion(String deployableComponentVersion) {
		this.deployableComponentVersion = deployableComponentVersion;
	}

	public void setPublishedVersion(String publishedVersion) {
		this.publishedVersion = publishedVersion;
	}


	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public void setArtifactRepository(String artifactRepository) {
		this.artifactRepository = artifactRepository;
	}

	public void setNexusUsername(String nexusUsername) {
		this.nexusUsername = nexusUsername;
	}

	public void setNexusPassword(String nexusPassword) {
		this.nexusPassword = nexusPassword;
	}

	public abstract String getVerb();

	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append(getVerb()).append(' ').append(deployableComponent.getName());
		sb.append(" --artifact-repository ").append(artifactRepository);
		if (snapshot) {
			sb.append("/content/repositories/snapshots");
		} else {
			sb.append("/content/repositories/releases");
		}
		sb.append(" --username ").append(nexusUsername);
		sb.append(" --password ").append(nexusPassword);
		sb.append(" --version ").append(deployableComponentVersion);
		sb.append(" -pv ").append(publishedVersion);
		sb.append(" --group ").append(groupId).append('.').append(deployableComponent.getPath());
		sb.append(" --artifactId ").append(deployableComponent.getName());
		if (snapshot) {
			sb.append(" --snapshot");
		}
		addSpecificParameters(sb);

		return sb.toString();
	}

	protected void addSpecificParameters(StringBuilder sb) {

	}

	@Override
	public String toString() {
		return "PublishCommand[" + deployableComponent + " " + deployableComponentVersion + "]";
	}


	//	|  publishJob name                                                publish deployableComponent to Artifact       |
//			|                                                                    Repository                    |
//			|      -r (--artifact-repository) url                                artifact repository           |
//			|      -u (--username) username                                      username                      |
//			|      -p (--password) password                                      password                      |
//			|      -v (--version) version                                        chooses a deployableComponent version         |
//			|      -pv (--publish-version) version                               chooses a publish version     |
//			|      -g (--group) group                                            chooses a deployableComponent group           |
//			|      -a (--artifactId) artifactId                                  published artifactId          |
//			|      -s (--snapshot)                                               publish as SNAPSHOT version   |
//			|      -t (--type) exportType                                        set export type, can be [osgi |
//			|                                                                    | standalone (or std)]; osgi  |
//			|                                                                    by default                    |
//			|      -jc (--deployableComponent-context) contextName                               specify which deployableComponent context you |
//			|                                                                    want to use through inputing  |
//			|                                                                    the deployableComponent context name          |
//			|      -jactc (--deployableComponent-apply-context-to-children)                      specify this parameter if you |
//			|                                                                    want to apply context to      |
//			|                                                                    children                      |

	//	|  publishService serviceName                                        publish service to Artifact   |
//			|                                                                    Repository                    |
//			|      -r (--artifact-repository) url                                artifact repository           |
//			|      -u (--username) username                                      username                      |
//			|      -p (--password) password                                      password                      |
//			|      -v (--version) version                                        chooses a service version     |
//			|      -pv (--publish-version) version                               chooses a publish version     |
//			|      -g (--group) group                                            chooses a service group       |
//			|      -a (--artifactId) artifactId                                  published artifactId          |
//			|      -s (--snapshot)                                               publish as SNAPSHOT version   |



//	|  publishRoute routeName                                            publish route to Artifact     |
//			|                                                                    Repository                    |
//			|      -r (--artifact-repository) url                                artifact repository           |
//			|      -u (--username) username                                      username                      |
//			|      -p (--password) password                                      password                      |
//			|      -v (--version) version                                        chooses a route version       |
//			|      -pv (--publish-version) version                               chooses a publish version     |
//			|      -g (--group) group                                            chooses a route group         |
//			|      -a (--artifactId) artifactId                                  published artifactId          |
//			|      -s (--snapshot)                                               publish as SNAPSHOT version   |


}
