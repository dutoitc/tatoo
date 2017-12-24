package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.PublishRouteCommand;
import ch.mno.tatoo.facade.commandline.data.Route;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class PublishRouteCommandTest {

    @Test
    public void test() {
        Route route = new Route("aRoute", "A_alpha.B_beta");
        route.setVersion(12.37f);
        PublishRouteCommand command = new PublishRouteCommand();
        command.setDeployableComponent(route);
        command.setDeployableComponentVersion("12.34");
        command.setPublishedVersion("56.78");
        command.setNexusUsername("tom");
        command.setNexusPassword("pouce");
        command.setGroupId("ch.mno.tatoo.app");

        command.setLastData("dummy");
        Assert.assertEquals("publishRoute aRoute --artifact-repository null/content/repositories/releases --username tom --password pouce --version 12.34 -pv 56.78 --group ch.mno.tatoo.app.A_alpha.B_beta --artifactId aRoute", command.build());
        Assert.assertEquals("Route A_alpha.B_beta:aRoute:12.37", route.toString());
    }


}
