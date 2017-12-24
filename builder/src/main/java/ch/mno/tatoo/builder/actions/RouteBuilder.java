package ch.mno.tatoo.builder.actions;

import ch.mno.tatoo.builder.Context;
import ch.mno.tatoo.builder.Report;
import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;
import ch.mno.tatoo.facade.commandline.commands.PublishRouteCommand;
import ch.mno.tatoo.facade.commandline.data.Route;

/**
 * Created by dutoitc on 04/10/17.
 */
public class RouteBuilder extends AbstractBuilder<Route> {

    public RouteBuilder(Context context, String publishedVersion, TalendCommandLineWrapper cmdline) {
        super(context, publishedVersion, cmdline);
    }

    @Override
    public void execute(Route route, Report report) {
        try {
            PublishRouteCommand command = new PublishRouteCommand();
            command.setDeployableComponent(route);
            command.setDeployableComponentVersion("" + route.getVersion());
            command.setPublishedVersion(publishedVersion);
            command.setSnapshot(false);
            command.setArtifactRepository(context.getNexusURL());
            command.setNexusUsername(context.getNexusUsername());
            command.setNexusPassword(context.getNexusPassword());

            cmdline.execute(command);
            report.addRouteBuilt("Route published: " + route.getFullName());
        }
        catch (Exception e) {
            report.addError("Route ko: " + route.getFullName() + " (" + e.getClass().getName() + ")");
        }
    }
}
