package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.facade.envt.EnvironmentReportForActions;
import ch.mno.tatoo.facade.envt.InstanceChecker;
import ch.mno.tatoo.facade.envt.ServiceChecker;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public class StatusCommand extends AbstractCommand {


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("status", "Affiche le status de Tatoo (instances et services).")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size()==1 && args.get(0).equals("status")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size()==1 && args.get(0).equals("status")) {
            checkInstances();
            checkServices();
        }
    }

    private void checkInstances() {
        EnvironmentReportForActions report = new EnvironmentReportForActions();
        String serverDS = properties.get(RuntimeProperties.PROPERTIES.SERVER_DS);
        String serverMDM = properties.get(RuntimeProperties.PROPERTIES.SERVER_NET);
        String tacSuffix = properties.get(RuntimeProperties.PROPERTIES.TAC_SUFFIX);
        new InstanceChecker(report).check(serverDS, serverMDM, tacSuffix);

        report.getErrorsList().forEach(s->reporter.logError(s));
        report.getInfos().forEach(s->reporter.logInfo(s));
    }

    private void checkServices() {
        List<String> blacklist = Arrays.asList(properties.get(RuntimeProperties.PROPERTIES.BLACKLIST).split(";"));

        EnvironmentReportForActions report = new EnvironmentReportForActions();
        String dbUri = properties.get(RuntimeProperties.PROPERTIES.DB_URI);
        String dbUser = properties.get(RuntimeProperties.PROPERTIES.DB_USERNAME);
        String dbPass = properties.get(RuntimeProperties.PROPERTIES.DB_PASSWORD);
        String prefix = properties.get(RuntimeProperties.PROPERTIES.WEB_PREFIX);
        String serverDS = properties.get(RuntimeProperties.PROPERTIES.SERVER_DS);
        String serverEnv = properties.get(RuntimeProperties.PROPERTIES.ENV);
        try {
            new ServiceChecker(dbUri, dbUser, dbPass, prefix, serverDS, serverEnv, report, blacklist).checkServices(false);
        } catch (Exception e) {
            report.reportError("Erreur lors de l'appel: " + e.getMessage());
        }

        report.getInfos().forEach(s->reporter.logInfo(s));
        report.getErrorsList().forEach(s->reporter.logError(s));
    }


}
