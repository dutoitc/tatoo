package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.facade.tac.TACFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * Created by dutoitc on 23/11/17.
 */
public class ExecuteCommand extends AbstractCommand {

    private static final Logger LOG = LoggerFactory.getLogger(ExecuteCommand.class);


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("execute [jobname] [jobname] [jobname]...", "Execute a job, given by its name")
        );
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() >= 2 && args.get(0).equals("execute")) {
            TACFacade tacFacade = buildTACFacade();
            for (String jobName: args.subList(1, args.size())) {
                if (jobName.trim().length()==0) continue;
                try {
                    reporter.logInfo("Exécution du job " + jobName);
                    tacFacade.executeTask(jobName);
                } catch (Exception e) {
                    if (LOG.isTraceEnabled()) {
                        e.printStackTrace();
                    }
                    reporter.logError("Erreur à l'exécution du job: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }


}
