package ch.mno.tatoo.cli.command;

import java.util.Arrays;
import java.util.List;

/**
 * Undeploy ESBTasks (services, routes) (do not uninstall)
 * Created by dutoitc on 23/11/17.
 */
public class UndeployCommand extends AbstractCommand {


    public void postInit() {
        super.postInit();
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("undeploy [regex]", "Dé-Déploier les routes, services (déjà installés)"),
                new UsageItem("undeploy:routes [regex]", "Dé-Déploier les routes (déjà installées)"),
                new UsageItem("undeploy:services [regex]", "Dé-Déploier les services, services (déjà installés)")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).startsWith("undeploy")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() < 1) return;
        if (args.size() < 2) throw new RuntimeException("Le regex est obligatoire");
        String regex = args.get(1);

        try {
            switch (args.get(0)) {
                case "undeploy":
                    buildTACFacade().undeployEsbTasks(regex, reporter, true, true);
                    break;
                case "undeploy:routes":
                    buildTACFacade().undeployEsbTasks(regex, reporter, false, true);
                    break;
                case "undeploy:services":
                    buildTACFacade().undeployEsbTasks(regex, reporter, true, false);
                    break;
            }
        } catch (Exception e) {
            reporter.logError("Erreur au " + args.get(0) + e.getMessage());
        }
    }


}