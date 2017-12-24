package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.karaf.commands.KarafCleanAction;
import ch.mno.tatoo.facade.tac.TACFacade;
import ch.mno.tatoo.facade.tac.commands.ListExecutionPlansCommand;
import ch.mno.tatoo.facade.tac.data.ESBTask;
import ch.mno.tatoo.facade.tac.data.ExecutionPlan;
import ch.mno.tatoo.facade.tac.data.JobTask;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Cleanup; le pattern est cherché dans les libellés des jobs, service, route et Karaf Bundle, ...
 * Created by dutoitc on 23/11/17.
 */
public class CleanupCommand extends AbstractCommand {


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("cleanup [regex]", "Supprime les jobs, routes, services, executionPlan dans le TAC, bundles et features dans Karaf")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("cleanup")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("cleanup")) {
            String pattern = ".*";
            if (args.size() > 1) {
                pattern = args.get(1);
            }
            try {
                cleanup(pattern);
            } catch (Exception e) {
                e.printStackTrace();
                reporter.logError("Erreur au clean: " + e.getMessage());
            }
        }
    }

    private void cleanup(String patternStr) throws Exception {
        Pattern pattern = Pattern.compile(patternStr);
        String serverDS = properties.get(RuntimeProperties.PROPERTIES.SERVER_DS);
        String serverMDM = properties.get(RuntimeProperties.PROPERTIES.SERVER_NET);
        String tacUrl = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_URL);
        String tacUsername = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_USERNAME);
        String tacPassword = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_PASSWORD);
        String tacEmail = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_EMAIL);
        String tacSuffix = properties.get(RuntimeProperties.PROPERTIES.TAC_SUFFIX);
        String karafHostname = properties.get(RuntimeProperties.PROPERTIES.KARAF_HOSTNAME);
        int karafPort = Integer.parseInt(properties.get(RuntimeProperties.PROPERTIES.KARAF_PORT));
        String karafUsername = properties.get(RuntimeProperties.PROPERTIES.KARAF_USERNAME);
        String karafPassword = properties.get(RuntimeProperties.PROPERTIES.KARAF_PASSWORD);
        String appGroupId = properties.get(RuntimeProperties.PROPERTIES.APPLICATION_GROUPID);

        TACFacade tacFacade = new TACFacade(tacUrl, tacUsername, tacPassword, tacEmail);
        List<JobTask> tasksJob = tacFacade.getTasksRelatedToJobs();
        Map<String, JobTask> tasksByName = tasksJob.stream().collect(Collectors.toMap(JobTask::getApplicationName, Function.identity()));

        Set<ExecutionPlan> plansToBeDeleted = new HashSet<>();
        Set<JobTask> tasksToBeDeleted = new HashSet<>();
        Set<ESBTask> esbTasksToBeDeleted = new HashSet<>();


        // TAC: Identify plans for matching jobs/tasks
        List<ExecutionPlan> executionPlans = tacFacade.execute(new ListExecutionPlansCommand());
        executionPlans.forEach(plan-> {
            plan.getJobs().stream()
                    .map(j -> tasksByName.get(j))
                    .filter(j -> pattern.matcher(j.getApplicationName()).find())
                    .forEach(j->reporter.logInfo("Attention: le job " + j.getLabel() + " a été détecté dans le plan " + plan.getLabel() + " ! (il faut supprimer le plan avant le job)"));
        });

        // TAC: Identify execution plans + tasks belonging to them
        executionPlans = tacFacade.execute(new ListExecutionPlansCommand());
        executionPlans.stream()
                .filter(plan -> pattern.matcher(plan.getLabel()).find() || plansToBeDeleted.contains(plan))
                .peek(plan -> plansToBeDeleted.add(plan))  // add plan to plansToBeDeleted
                .flatMap(plan -> plan.getJobs().stream())
                .map(j -> tasksByName.get(j))
                .forEach(j -> tasksToBeDeleted.add(j));  // add plan's tasks to tasksToBeDeleted

        // TAC: Identify Tasks
        tacFacade.getTasksRelatedToJobs().stream() // TODO: or getTasks() ?
                .filter(j -> pattern.matcher(j.getApplicationName()).find())
                .forEach(j -> tasksToBeDeleted.add(j));

        // TAC: routes, services
        tacFacade.getESBTasks().stream()
                .filter(j -> pattern.matcher(j.getApplicationName()).find())
                .forEach(j -> esbTasksToBeDeleted.add(j));

        reporter.logInfo("Plans d'exécution à supprimer: " + plansToBeDeleted.size());
        plansToBeDeleted.forEach(plan -> reporter.logInfo("  - " + plan.getLabel()));
        reporter.logInfo("Task à supprimer (jobs): " + tasksToBeDeleted.size());
        tasksToBeDeleted.forEach(task -> reporter.logInfo("  - " + task.getLabel()));
        reporter.logInfo("ESBTask à supprimer (service, route): " + esbTasksToBeDeleted.size());
        esbTasksToBeDeleted.forEach(task -> reporter.logInfo("  - " + task.getLabel()));


        // ---------------------------------------------------------------------------------------------------

        if (!properties.isDryRun()) {

            // TAC: execution plans
            plansToBeDeleted.forEach(plan -> {
                try {
                    tacFacade.deletePlan(plan.getPlanId());
                    reporter.logInfo("Supprimé le plan " + plan.getLabel());
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppresion du plan " + plan.getLabel());
                }
            });


            // TAC: Delete TASK (jobs)
            tasksToBeDeleted.forEach(t -> {
                try {
                    tacFacade.deleteTask(t.getId(), true);
                    reporter.logInfo("Supprimé le job " + t.getLabel());
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppresion du job " + t.getLabel()+": " + e.getMessage());
                }
            });


            // TAC: delete ESBTask (routes, services)
            esbTasksToBeDeleted.forEach(task -> {
                try {
                    tacFacade.deleteTask(task.getId(), true);
                    reporter.logInfo("Suprimé l'EsbTask " + task.getLabel() + " (" + task.getApplicationType() + ")");
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppresion de l'EsbTask " + task.getLabel());
                }
            });


            // Karaf
            KarafFacade kf = new KarafFacade(karafHostname, karafPort, karafUsername, karafPassword);
            kf.execute(new KarafCleanAction(appGroupId, patternStr));

        }


    }
}