package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.PublishServiceCommand;
import ch.mno.tatoo.facade.commandline.data.Service;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class PublishServiceCommandTest {

    @Test
    public void test() {
        Service service = new Service("aService", "A_alpha.B_beta");
        service.setVersion(12.37f);
        PublishServiceCommand command = new PublishServiceCommand();
        command.setDeployableComponent(service);
        command.setDeployableComponentVersion("12.34");
        command.setPublishedVersion("56.78");
        command.setNexusUsername("tom");
        command.setNexusPassword("pouce");
        command.setGroupId("ch.mno.tatoo.app");

        command.setLastData("dummy");
        Assert.assertEquals("publishService aService --artifact-repository null/content/repositories/releases --username tom --password pouce --version 12.34 -pv 56.78 --group ch.mno.tatoo.app.A_alpha.B_beta --artifactId aService", command.build());
        Assert.assertEquals("Service A_alpha.B_beta:aService:12.37", service.toString());
    }


}
