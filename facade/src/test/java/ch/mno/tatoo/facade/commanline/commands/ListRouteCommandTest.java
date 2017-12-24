package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.ListRouteCommand;
import ch.mno.tatoo.facade.commandline.data.Route;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class ListRouteCommandTest {

    @Test
    public void test() {
        ListRouteCommand command = new ListRouteCommand(Pattern.compile(".*alpha.*"));
        command.setLastData("[Standard Jobs]\n" +
                "  [A_alpha]\n" +
                "    [B_beta]\n" +
                "      JOB_Gamma\n" +
                "      JOB_Delta\n" +
                "    [E_Epsilon]\n" +
                "      JOB_Iota\n" +
                "      JOB_Lambda");
        Assert.assertEquals("listRoute", command.build());
        List<Route> routes = command.extractData();
        Assert.assertEquals(4, routes.size());
        Route route = routes.get(0);
        Assert.assertEquals("JOB_Gamma", route.getName());
        Assert.assertEquals("A_alpha.B_beta.JOB_Gamma", route.getFullName());
        Assert.assertEquals(null, route.getVersion());
    }

}
