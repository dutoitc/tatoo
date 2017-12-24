package ch.mno.tatoo.facade.commanline.commands;

import ch.mno.tatoo.facade.commandline.commands.ListJobCommand;
import ch.mno.tatoo.facade.commandline.data.Job;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class ListJobCommandTest {

    @Test
    public void test() {
        ListJobCommand command = new ListJobCommand(Pattern.compile(".*alpha.*"));
        command.setLastData("[Standard Jobs]\n" +
                "  [A_alpha]\n" +
                "    [B_beta]\n" +
                "      JOB_Gamma\n" +
                "      JOB_Delta\n" +
                "    [E_Epsilon]\n" +
                "      JOB_Iota\n" +
                "      JOB_Lambda");
        Assert.assertEquals("listJob", command.build());
        List<Job> jobs = command.extractData();
        Assert.assertEquals(4, jobs.size());
        Job job = jobs.get(0);
        Assert.assertEquals("JOB_Gamma", job.getName());
        Assert.assertEquals("A_alpha.B_beta.JOB_Gamma", job.getFullName());
        Assert.assertEquals(null, job.getVersion());
    }

}
