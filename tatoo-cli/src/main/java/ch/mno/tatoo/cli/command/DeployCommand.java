package ch.mno.tatoo.cli.command;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public class DeployCommand extends AbstractCommand {

    public void postInit() {
        super.postInit();
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("deploy [regex]", "Déploier les jobs, routes, services (déjà installés)"),
                new UsageItem("deploy:routes [regex]", "Déploier les routes (déjà installées)"),
                new UsageItem("deploy:services [regex]", "Déploier les services, services (déjà installés)"),
                new UsageItem("deploy:jobs [regex]", "Déploier les jobs (déjà installés)")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).startsWith("deploy")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() < 1) return;
        if (args.size() < 2) throw new RuntimeException("Le regex est obligatoire");
        String regex = args.get(1);

        try {
            switch (args.get(0)) {
                case "deploy":
                    buildTACFacade().deployEsbTasks(regex, reporter, true, false); // Services first
                    buildTACFacade().deployEsbTasks(regex, reporter, false, true); // Routes then
                    buildTACFacade().deployTasks(regex, reporter);
                    break;
                case "deploy:routes":
                    buildTACFacade().deployEsbTasks(regex, reporter, false, true);
                    break;
                case "deploy:services":
                    buildTACFacade().deployEsbTasks(regex, reporter, true, false);
                    break;
                case "deploy:jobs":
                    buildTACFacade().deployTasks(regex, reporter);
                    break;
            }
        } catch (Exception e) {
            reporter.logError("Erreur au " + args.get(0) + e.getMessage());
        }
    }


}