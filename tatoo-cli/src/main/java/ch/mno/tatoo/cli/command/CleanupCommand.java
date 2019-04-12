package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.karaf.KarafSSHFacade;
import ch.mno.tatoo.facade.karaf.commands.KarafCleanAction;
import ch.mno.tatoo.facade.tac.TACFacade;
import ch.mno.tatoo.facade.tac.commands.ListExecutionPlansCommand;
import ch.mno.tatoo.facade.tac.data.ESBTask;
import ch.mno.tatoo.facade.tac.data.ExecutionPlan;
import ch.mno.tatoo.facade.tac.data.FEATURE_TYPE;
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
                new UsageItem("cleanup [regex]", "Supprime les jobs, routes, services, executionPlan dans le TAC, bundles et features dans Karaf"),
                new UsageItem("cleanup:job [regex]", "Supprime les jobs, executionPlan dans le TAC, bundles et features dans Karaf"),
                new UsageItem("cleanup:service_route [regex]", "Supprime les routes, services, executionPlan dans le TAC, bundles et features dans Karaf")
        );
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() > 0 && (args.get(0).equals("cleanup") || args.get(0).equals("cleanup:job") || args.get(0).equals("cleanup:service_route"))) {
            handleCommandCleanup(args);
        }
    }

    private void handleCommandCleanup(List<String> args) {
        String verb = args.get(0);

        // Read pattern
        String pattern = ".*";
        if (args.size() > 1) {
            pattern = args.get(1);
        }

        // Remove ",'
        if ((pattern.startsWith("\"") && pattern.endsWith("\"")) || (pattern.startsWith("'") && pattern.endsWith("'"))) {
            pattern = pattern.substring(1, pattern.length()-1);
        }

        try {
            switch(verb) {
                case "cleanup":
                    cleanup(pattern, true, true);
                    break;
                case "cleanup:job":
                    cleanup(pattern, true, false);
                    break;
                case "cleanup:service_route":
                    cleanup(pattern, false, true);
                    break;
                default:
                    throw new RuntimeException("Unsupported verb " + verb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reporter.logError("Erreur au clean: " + e.getMessage());
        }
    }


    private void cleanup(String patternStr, boolean job, boolean serviceRoute) throws Exception {
        Pattern pattern = Pattern.compile(patternStr);
        TACFacade tacFacade = buildTACFacade();


        // Base data
        Map<String, JobTask> tasksByName = tacFacade.getTasksRelatedToJobs().stream().collect(Collectors.toMap(JobTask::getApplicationName, Function.identity()));
        Set<ExecutionPlan> plansToBeDeleted = new HashSet<>();
        Set<JobTask> tasksToBeDeleted = new HashSet<>();
        Set<ESBTask> esbTasksToBeDeleted = new HashSet<>();


        // Find plans and tasks to be deleted
        List<ExecutionPlan> executionPlans = tacFacade.execute(new ListExecutionPlansCommand());
        if (job) {
            // TAC: Identify plans for matching jobs/tasks
            executionPlans.forEach(plan -> plan.getJobs().stream()
                    .map(j -> tasksByName.get(j))
                    .peek(j -> {
                        if (j == null)
                            System.err.println("ATTENTION: pas de tâche trouvée avec le nom " + j + " dans le plan " + plan.getLabel());
                    }) // TODO: est-ce grâve ?
                    .filter(j -> j != null)
                    .filter(j -> pattern.matcher(j.getApplicationName()).find())
                    .forEach(j -> reporter.logInfo("Attention: le job " + j.getLabel() + " a été détecté dans le plan " + plan.getLabel() + " ! (il faut supprimer le plan avant le job)")));

            // TAC: Identify execution plans + tasks belonging to them
            executionPlans.stream()
                    .filter(plan -> pattern.matcher(plan.getLabel()).find() || plansToBeDeleted.contains(plan))
                    .peek(plan -> plansToBeDeleted.add(plan))  // add plan to plansToBeDeleted
                    .flatMap(plan -> plan.getJobs().stream())
                    .map(j -> tasksByName.get(j))
                    .forEach(j -> tasksToBeDeleted.add(j));  // add plan's tasks to tasksToBeDeleted

            // TAC: Identify Tasks
            tacFacade.getTasksRelatedToJobs().stream() // TODO: or getTasks() ?
                    .filter(j -> pattern.matcher(j.getApplicationName()).find())
                    .peek(j-> { if (j==null) System.err.println("ATTENTION: pas de tâche trouvée avec le nom " + j);}) // TODO: est-ce grâve ?
                    .filter(j->j!=null)
                    .forEach(j -> tasksToBeDeleted.add(j));
        }

        // TAC: routes, services
        tacFacade.getESBTasks().stream()
                .filter(j -> pattern.matcher(j.getApplicationName()).find())
                .filter(j-> (j.getApplicationType().equals(FEATURE_TYPE.JOB) && job) ||
                        (j.getApplicationType().equals(FEATURE_TYPE.SERVICE) && serviceRoute) ||
                        (j.getApplicationType().equals(FEATURE_TYPE.ROUTE) && serviceRoute) ||
                        j.getApplicationType().equals(FEATURE_TYPE.GENERIC)
                )
                .forEach(j -> esbTasksToBeDeleted.add(j));

        reporter.logInfo("Plans d'exécution à supprimer: " + plansToBeDeleted.size());
        plansToBeDeleted.stream().filter(f->f!=null).forEach(plan -> reporter.logInfo("  - " + plan.getLabel()));
        reporter.logInfo("Task à supprimer (jobs): " + tasksToBeDeleted.size());
        tasksToBeDeleted.stream().filter(f->f!=null).forEach(task -> reporter.logInfo("  - " + task.getLabel()));
        reporter.logInfo("ESBTask à supprimer (service, route): " + esbTasksToBeDeleted.size());
        esbTasksToBeDeleted.stream().filter(f->f!=null).forEach(task -> reporter.logInfo("  - " + task.getLabel()));


        // ---------------------------------------------------------------------------------------------------

        if (!properties.isDryRun()) {
            // TAC: Delete TASK (jobs)
            tasksToBeDeleted.stream().filter(t->t!=null).forEach(t -> {
                try {
                    tacFacade.deleteTask(t.getId(), true);
                    reporter.logInfo("Supprimé le job " + t.getLabel());
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppresion du job " + t.getLabel()+": " + e.getMessage());
                }
            });


            // TAC: delete ESBTask (routes, services)
            esbTasksToBeDeleted.stream().filter(t->t!=null).forEach(task -> {
                try {
                    tacFacade.deleteTask(task.getId(), true);
                    reporter.logInfo("Supprimé l'EsbTask " + task.getLabel() + " (" + task.getApplicationType() + ")");
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppresion de l'EsbTask " + task.getLabel());
                }
            });

            // TAC: execution plans
            plansToBeDeleted.stream().filter(t->t!=null).forEach(plan -> {
                try {
                    tacFacade.deletePlan(plan.getPlanId());
                    reporter.logInfo("Supprimé le plan " + plan.getLabel());
                } catch (Exception e) {
                    reporter.logError("Erreur à la suppression du plan " + plan.getLabel());
                }
            });


            // Karaf
            if (serviceRoute) {
                try (
                        KarafFacade kf = buildKarafFacade();
                ) {
                    kf.execute(new KarafCleanAction(patternStr));
                }
            }

        }
    }

}
