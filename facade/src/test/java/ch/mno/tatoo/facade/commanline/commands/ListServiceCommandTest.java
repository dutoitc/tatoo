package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.ListServiceCommand;
import ch.mno.tatoo.facade.commandline.data.Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class ListServiceCommandTest {

    @Test
    public void test() {
        ListServiceCommand command = new ListServiceCommand(Pattern.compile(".*alpha.*"));
        command.setLastData("[Standard Jobs]\n" +
                "  [A_alpha]\n" +
                "    [B_beta]\n" +
                "      JOB_Gamma\n" +
                "      JOB_Delta\n" +
                "    [E_Epsilon]\n" +
                "      JOB_Iota\n" +
                "      JOB_Lambda");
        Assert.assertEquals("listService", command.build());
        List<Service> services = command.extractData();
        Assert.assertEquals(4, services.size());
        Service service = services.get(0);
        Assert.assertEquals("JOB_Gamma", service.getName());
        Assert.assertEquals("A_alpha.B_beta.JOB_Gamma", service.getFullName());
        Assert.assertEquals(null, service.getVersion());
    }

}
