package ch.mno.tatoo.builder.actions;

import ch.mno.tatoo.builder.Context;
import ch.mno.tatoo.builder.Report;
import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;
import ch.mno.tatoo.facade.commandline.commands.PublishJobCommand;
import ch.mno.tatoo.facade.commandline.commands.PublishServiceCommand;
import ch.mno.tatoo.facade.commandline.data.Job;

/**
 * Created by dutoitc on 04/10/17.
 */
public class JobBuilder extends AbstractBuilder<Job> {

    public JobBuilder(Context context, String publishedVersion, TalendCommandLineWrapper cmdline) {
        super(context, publishedVersion, cmdline);
    }

    @Override
    public void execute(Job job, Report report) {
        try {
            if (job.getName().contains("WS_")) {
                PublishServiceCommand command = new PublishServiceCommand();
                command.setGroupId(context.get(Context.PROPERTIES.APPLICATION_GROUPID)+".service");
                command.setDeployableComponent(job);
                command.setDeployableComponentVersion("" + job.getVersion());
                command.setPublishedVersion(publishedVersion);
                command.setSnapshot(false);
                command.setArtifactRepository(context.getNexusURL());
                command.setNexusUsername(context.getNexusUsername());
                command.setNexusPassword(context.getNexusPassword());
                cmdline.execute(command);
                report.addJobBuilt(job.getFullName());
            } else {
                PublishJobCommand command = new PublishJobCommand();command.setDeployableComponent(job);
                command.setDeployableComponentVersion("" + job.getVersion());
                command.setPublishedVersion(publishedVersion);
                command.setOsgi(!job.getName().startsWith("SOCLE") && !job.getName().startsWith("PREDEPLOY") && !job.getName().startsWith("POSTDEPLOY") &&
                        !job.getName().startsWith("INFRA") && !job.getName().startsWith("JOB"));
                command.setSnapshot(false);
                command.setArtifactRepository(context.getNexusURL());
                command.setNexusUsername(context.getNexusUsername());
                command.setNexusPassword(context.getNexusPassword());
                cmdline.execute(command);
                report.addJobBuilt(job.getFullName());
            }

        }
        catch (Exception e) {
            if (job.getFullName().contains("WS_")) {
                report.addError("Job WS ko: " + job.getFullName() + " (" + e.getClass().getName() + ")\n");
            } else {
                report.addError("Job ko: " + job.getFullName() + " (" + e.getClass().getName() + ")\n");
            }
        }
    }
}
