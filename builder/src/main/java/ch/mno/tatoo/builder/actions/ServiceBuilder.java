package ch.mno.tatoo.builder.actions;

import ch.mno.tatoo.builder.Context;
import ch.mno.tatoo.builder.Report;
import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;
import ch.mno.tatoo.facade.commandline.commands.PublishServiceCommand;
import ch.mno.tatoo.facade.commandline.data.Service;

/**
 * Created by dutoitc on 04/10/17.
 */
public class ServiceBuilder extends AbstractBuilder<Service> {

    public ServiceBuilder(Context context, String publishedVersion, TalendCommandLineWrapper cmdline) {
        super(context, publishedVersion, cmdline);
    }

    @Override
    public void execute(Service service, Report report) {
        try {
            PublishServiceCommand command = new PublishServiceCommand();
            command.setGroupId(context.get(Context.PROPERTIES.APPLICATION_GROUPID));
            command.setDeployableComponent(service);
            command.setDeployableComponentVersion("" + service.getVersion());
            command.setPublishedVersion(publishedVersion);
            command.setSnapshot(false);
            command.setArtifactRepository(context.getNexusURL());
            command.setNexusUsername(context.getNexusUsername());
            command.setNexusPassword(context.getNexusPassword());

            cmdline.execute(command);
            report.addServiceBuilt(service.getFullName());
        }
        catch (Exception e) {
            report.addError("Service ko: " + service.getFullName() + " (" + e.getClass().getName() + ")");
        }
    }
}
