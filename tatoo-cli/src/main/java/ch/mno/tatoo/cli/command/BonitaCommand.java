package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.facade.bonita.BonitaDeployReport;
import ch.mno.tatoo.facade.bonita.BonitaFacade;
import ch.mno.tatoo.facade.bonita.BonitaProcess;
import ch.mno.tatoo.facade.bonita.BonitaUndeployReport;
import ch.mno.tatoo.facade.common.FacadeException;
import ch.mno.tatoo.facade.karaf.KarafSSHFacade;
import ch.mno.tatoo.facade.karaf.commands.KarafCleanAction;
import ch.mno.tatoo.facade.tac.TACFacade;
import ch.mno.tatoo.facade.tac.commands.ListExecutionPlansCommand;
import ch.mno.tatoo.facade.tac.data.ESBTask;
import ch.mno.tatoo.facade.tac.data.ExecutionPlan;
import ch.mno.tatoo.facade.tac.data.JobTask;
import org.bonitasoft.engine.exception.*;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Bonita API commands
 * Created by dutoitc on 23/11/17.
 */
public class BonitaCommand extends AbstractCommand {


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("bonita:bpm_deploy [bar]( [bar])*", "Installe un ou plusieurs fichiers .BAR"),
                new UsageItem("bonita:bpm_undeploy [name:version]( [name:version])*", "Désinstalle un ou plusieurs fichiers BPM"),
                new UsageItem("bonita:bpm_list", "Liste les BPM installés"),
                new UsageItem("bonita:clean_process_instances", "Supprime toutes les instances de process existantes")
        );
    }

    @Override
    public void handle(List<String> args) {
        if (args.isEmpty()) return;
        switch (args.get(0)) {
            case "bonita:bpm_deploy":
                handleBpmDeploy(args.subList(1, args.size()));
                break;
            case "bonita:bpm_undeploy":
                handleBpmUndeploy(args.subList(1, args.size()));
                break;
            case "bonita:bpm_list":
                handleBpmList(args);
                break;
            case "bonita:clean_process_instances":
                handleBonitaCleanProcessInstances();
                break;
        }
    }

    private void handleBpmList(List<String> args) {
        try {
            List<BonitaProcess> report = buildBonitaFacade().findProcesses(null);
            report.stream().forEach(System.out::println);
        } catch (FacadeException e) {
            e.printStackTrace();
        }
    }

    private void handleBpmDeploy(List<String> files) {
        try {
            BonitaDeployReport report = buildBonitaFacade().deployFiles(files);
            System.out.println(report);
        } catch (FacadeException e) {
            e.printStackTrace();
        }
    }

    private void handleBpmUndeploy(List<String> names) {
        try {
            BonitaUndeployReport report = buildBonitaFacade().undeploy(names);
            System.out.println(report);
        } catch (FacadeException e) {
            e.printStackTrace();
        }
    }

    private void handleBonitaCleanProcessInstances() {
        BonitaFacade facade = buildBonitaFacade();
        try {
            facade.findProcesses(null).forEach(p -> {
                try {
                    long nbCases = facade.countProcessInstances(p.getProcessId());
                    System.out.println("Deleting process instances of #" + p.getId() + " - " + p.getName() + ":" + p.getVersion() + ", nbCases=" + nbCases);
                    facade.deleteProcessInstances(p.getProcessId(), (int)nbCases);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (FacadeException e) {
            e.printStackTrace();
        }
    }
}
