package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.facade.common.FacadeException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 27/04/18.
 */
public class TacCommand extends AbstractCommand {
    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("tac:pauseTasks", "Pause all Tasks (Jobs but not execution plan)")
        );
    }


    @Override
    public void handle(List<String> args) {
        if (args.size() < 1) return;

        if (args.get(0).equals("tac:pauseTasks")) {
            try {
                buildTACFacade().pauseTasks();
            } catch (FacadeException e) {
                e.printStackTrace();
            }
        }
    }

}
