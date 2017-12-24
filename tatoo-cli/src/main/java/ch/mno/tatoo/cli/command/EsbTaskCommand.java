package ch.mno.tatoo.cli.command;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public class EsbTaskCommand extends AbstractCommand {

    public void postInit() {
        super.postInit();
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("start [regex]", "Démarrer les services, routes déjà installés"),
                new UsageItem("start:services [regex]", "Démarrer les services déjà installés"),
                new UsageItem("start:routes [regex]", "Démarrer les routes déjà installés"),
                new UsageItem("stop [regex]", "Stopper les services, routes déjà installés"),
                new UsageItem("stop:services [regex]", "Stopper les services déjà installés"),
                new UsageItem("stop:routes [regex]", "Stopper les routes déjà installés")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size()!=2) return false;
        switch (args.get(0)) {
            case "start":
            case "start:services":
            case "start:routes":
            case "stop":
            case "stop:services":
            case "stop:routes":
                return true;
        }
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size()<1) return;
        if (args.size() < 2) throw new RuntimeException("Le regex est obligatoire");
        String regex = args.get(1);

        try {
            switch (args.get(0)) {
                case "start":
                    buildTACFacade().startEsbTasks(regex, reporter, true, false);
                    buildTACFacade().startEsbTasks(regex, reporter, false, true);
                    break;
                case "start:services":
                    buildTACFacade().startEsbTasks(regex, reporter, true, false);
                    break;
                case "start:routes":
                    buildTACFacade().startEsbTasks(regex, reporter, false, true);
                    break;
                case "stop":
                    buildTACFacade().stopEsbTasks(regex, reporter, false, true);
                    buildTACFacade().stopEsbTasks(regex, reporter, true, false);
                    break;
                case "stop:services":
                    buildTACFacade().stopEsbTasks(regex, reporter, true, false);
                    break;
                case "stop:routes":
                    buildTACFacade().stopEsbTasks(regex, reporter, false, true);
                    break;
            }
        } catch (Exception e) {
            reporter.logError("Erreur au " + args.get(0) + e.getMessage());
        }
    }

}