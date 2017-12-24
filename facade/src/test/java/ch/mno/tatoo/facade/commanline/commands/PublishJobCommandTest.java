package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.PublishJobCommand;
import ch.mno.tatoo.facade.commandline.data.Job;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class PublishJobCommandTest {

    @Test
    public void testStandalone() {
        Job job = new Job("aName", "A_alpha.B_beta");
        job.setVersion(12.37f);
        PublishJobCommand command = new PublishJobCommand();
        command.setDeployableComponent(job);
        command.setDeployableComponentVersion("12.34");
        command.setPublishedVersion("56.78");
        command.setNexusUsername("tom");
        command.setNexusPassword("pouce");
        command.setGroupId("ch.mno.tatoo.app");
        command.setLastData("dummy");
        Assert.assertEquals("publishJob aName --artifact-repository null/content/repositories/releases --username tom --password pouce --version 12.34 -pv 56.78 --group ch.mno.tatoo.app.A_alpha.B_beta --artifactId aName --type standalone", command.build());
        Assert.assertEquals("Job A_alpha.B_beta:aName:12.37", job.toString());
    }

    @Test
    public void testOsgi() {
        Job job = new Job("aName", "A_alpha.B_beta");
        PublishJobCommand command = new PublishJobCommand();
        command.setDeployableComponentVersion("12.34");
        command.setPublishedVersion("56.78");
        command.setNexusUsername("tom");
        command.setNexusPassword("pouce");
        command.setGroupId("ch.mno.tatoo.app");
        command.setOsgi(true);
        command.setDeployableComponent(job);

        command.setLastData("dummy");
        Assert.assertEquals("publishJob aName --artifact-repository null/content/repositories/releases --username tom --password pouce --version 12.34 -pv 56.78 --group ch.mno.tatoo.app.A_alpha.B_beta --artifactId aName --type osgi", command.build());
    }


}
