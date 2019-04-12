package ch.mno.tatoo.facade.tac;

import ch.mno.tatoo.common.reporters.Reporter;
import ch.mno.tatoo.facade.common.FacadeException;
import ch.mno.tatoo.facade.tac.commands.*;
import ch.mno.tatoo.facade.tac.data.ExecutionPlan;
import ch.mno.tatoo.facade.tac.data.JobTask;
import ch.mno.tatoo.facade.tac.data.ESBTask;
import ch.mno.tatoo.facade.tac.data.FEATURE_TYPE;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dutoitc on 12/08/15.
 */
public class TACFacade {

    private static final Logger LOG = LoggerFactory.getLogger(TACFacade.class);


    private String tacUrl;
    private String tacUsername;
    private String tacPassword;
    private String tacEmail;
    private MetaservletFacade facade;

    public TACFacade(String tacUrl, String tacUsername, String tacPassword, String tacEmail) {
        this.tacUrl = tacUrl;
        this.tacUsername = tacUsername;
        this.tacPassword = tacPassword;
        this.tacEmail = tacEmail;
        this.facade = new MetaservletFacade(tacUrl);
    }



    public <R> R execute(AbstractCommand command) throws FacadeException {
        try {
            long t0 = System.currentTimeMillis();
            LOG.info("Running " + command.toString());

            // Build URL from JSON request
            String json = command.buildJSON(tacUsername, tacPassword, tacEmail);
            System.out.println(json);
            String resultStr = facade.call(json);
            JSONObject result = (JSONObject) JSONValue.parse(resultStr);

            // Check return code
            RETURN_CODES returnCode = RETURN_CODES.fromCode((Long) result.get("returnCode"));
            if (returnCode != RETURN_CODES.SUCCESS) {
                StringBuilder sb = new StringBuilder();
                sb.append("Return code #" + result.get("returnCode")).append(":").append(result.get("error"));
                LOG.info("Execution done. " + command.toString() + " in " + (System.currentTimeMillis() - t0) / 1000 + "s");
                LOG.error("Command failed:\n" + json);
                throw new CallException(sb.toString(), command);
            }

            command.keepResults(result);
            LOG.info("Execution done. " + command.toString() + " in " + (System.currentTimeMillis() - t0) / 1000 + "s");
            return (R) command.getData();

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    public void deleteTask(int id, boolean deleteRelatedData) throws FacadeException {
        DeleteTaskCommand command = new DeleteTaskCommand(id, deleteRelatedData);
        execute(command);
    }

    public List<JobTask> getTasks() throws FacadeException {
        ListTasksCommand command = new ListTasksCommand();
        execute(command);
        return command.getJobTasks();
    }

    public List<ESBTask> getESBTasks() throws FacadeException {
        ListESBTasksCommand command = new ListESBTasksCommand();
        execute(command);
        return command.getJobTasks();
    }

    public List<JobTask> getTasksRelatedToJobs() throws FacadeException {
        GetTasksRelatedToJobsCommand command = new GetTasksRelatedToJobsCommand();
        execute(command);
        return command.getTasks();
    }

    public void listTasks() throws FacadeException {
        ListTasksCommand command = new ListTasksCommand();
        execute(command);
        for (JobTask jobTask : command.getJobTasks()) {
            System.out.println(jobTask.toTableString());
        }
    }

    public void pauseTasks() throws FacadeException {
        ListTasksCommand command = new ListTasksCommand();
        execute(command);
        for (JobTask jobTask : command.getJobTasks()) {
            execute(new PauseTaskCommand(String.valueOf(jobTask.getId())));
        }
    }

    /**
     * e.g. "INFRA_Service_Ping-feature", "666.0", "G_Technique"
     */
    public String saveESBService(String name, String version, String path, String serverLabel) throws FacadeException {
        SaveESBTaskCommand command = SaveESBTaskCommand.buildService(name, version, path, serverLabel);
        execute(command);
        return command.getStringResult();
    }

    /**
     * e.g. "INFRA_Route_Ping-feature", "666.0", "G_Technique"
     */
    public String saveESBRoute(String name, String version, String path, String serverLabel) throws FacadeException {
        SaveESBTaskCommand command = SaveESBTaskCommand.buildRoute(name, version, path, serverLabel);
        execute(command);
        return command.getStringResult();
    }

    public void deletePlan(long planId) {
        try {
            execute(new DeletePlanCommand(true, planId));
        } catch (Exception e) {
            throw new RuntimeException("Impossible de supprimer le plan " + planId + ": " + e.getMessage());
        }
    }

    private Stream<JobTask> findTasks(String regex) throws FacadeException {
        List<JobTask> tasks = execute(new ListTasksCommand());
        return tasks.stream()
                .peek(j -> System.out.println("Checking " + j.getApplicationName() + " for " + regex))
                .filter(j -> Pattern.compile(regex).matcher(j.getApplicationName()).find());
    }

    private Stream<ESBTask> findEsbTasks(String regex) throws FacadeException {
        List<ESBTask> tasks = execute(new ListESBTasksCommand());
        return tasks.stream()
//                .peek(j->System.out.println("Checking "+j.getApplicationName()+" for " + regex))
                .filter(j -> Pattern.compile(regex).matcher(j.getApplicationName()).find());
    }

    /**
     * Deploy Task (job)
     */
    public void deployTasks(String regex, Reporter reporter) throws FacadeException {
        findTasks(regex)
                .peek(c -> reporter.logInfo("Deploying task " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new RequestDeployTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reporter.logError("Erreur: " + e.getMessage());
                    }
                });
    }

    /**
     * Deploy ESBTasks (route, service) already installed)
     */
    public void deployEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws FacadeException {
        findEsbTasks(regex)
//                .peek(t->System.out.println("Check " + t.getApplicationType()+", "+(t.getApplicationType()== FEATURE_TYPE.SERVICE)))
                .filter(t -> (t.getApplicationType() == FEATURE_TYPE.SERVICE && services) || (t.getApplicationType() == FEATURE_TYPE.ROUTE && routes))
                .peek(c -> reporter.logInfo("Deploying ESBTask " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new RequestDeployEsbTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reporter.logError("Erreur: " + e.getMessage());
                    }
                });
    }

    /**
     * Deploy ESBTasks (route, service) already installed)
     */
    public void startEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws FacadeException {
        findEsbTasks(regex)
                .filter(t -> (t.getApplicationType() == FEATURE_TYPE.SERVICE && services) || (t.getApplicationType() == FEATURE_TYPE.ROUTE && routes))
                .peek(c -> reporter.logInfo("Starting ESBTask " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new StartEsbTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reporter.logError("Erreur: " + e.getMessage());
                    }
                });
    }

    /**
     * Stop ESBTasks (route, service) already installed)
     */
    public void stopEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws FacadeException {
        findEsbTasks(regex)
                .filter(t -> (t.getApplicationType() == FEATURE_TYPE.SERVICE && services) || (t.getApplicationType() == FEATURE_TYPE.ROUTE && routes))
                .peek(c -> reporter.logInfo("Stopping ESBTask " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new StopEsbTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reporter.logError("Erreur: " + e.getMessage());
                    }
                });
    }

    public void undeployEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws FacadeException {
        findEsbTasks(regex)
                .filter(t -> (t.getApplicationType() == FEATURE_TYPE.SERVICE && services) || (t.getApplicationType() == FEATURE_TYPE.ROUTE && routes))
                .peek(c -> reporter.logInfo("Undeploying ESBTask " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new RequestUndeployEsbTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        if (e.getMessage().contains("is not installed")) {
                            reporter.logError("Erreur: " + e.getMessage());
                        } else {
                            e.printStackTrace();
                            reporter.logError("Erreur: " + e.getMessage());
                        }
                    }
                });
    }

    /**
     * DaysOfMonth, DaysOfWeek: exclusifs. Choisir "*" si inutile. DaysOfWeek: 1=dimanche
     */
    public void createCronTrigger(String taskId, String label, String description, String hours, String minutes, String daysOfMonth, String daysOfWeek, String months, String years) throws FacadeException {
        CreateCronTriggerCommand command = new CreateCronTriggerCommand(label, taskId);
        command.setHours(hours);
        command.setMinutes(minutes);
        command.setDaysOfMonth(daysOfMonth);
        command.setDaysOfWeek(daysOfWeek);
        command.setDescription(description);
        command.setMonths(months);
        command.setYears(years);
        execute(command);
    }

    public void createTag(String projectName, String version, Reporter reporter) {
        try {
            CreateTagCommand command = new CreateTagCommand(projectName, version);
            execute(command);
            reporter.logInfo("Tag created: " + version + " on project " + projectName);
        } catch (Exception e) {
            reporter.logError("Cannot create tag " + version + ": " + e.getMessage());
        }
    }

    public long getTaskIdByName(String taskName) throws FacadeException {
        List<JobTask> lst = getTasks().stream()
                .filter(t -> t.getApplicationName().equals(taskName))
                .collect(Collectors.toList());
        if (lst.size() == 1) {
            return lst.get(0).getId();
        }
        if (lst.isEmpty()) {
            throw new RuntimeException("Task non trouvée: " + taskName);
        }
        throw new RuntimeException("Trop de Task trouvés pour: " + taskName);
    }

    public long getExecPlanIdByName(String execPlanName) throws FacadeException {
        List<ExecutionPlan> plans = execute(new ListExecutionPlansCommand());

        List<ExecutionPlan> lst = plans.stream()
                .filter(t -> t.getLabel().equals(execPlanName))
                .collect(Collectors.toList());
        if (lst.size() == 1) {
            return lst.get(0).getPlanId();
        }
        if (lst.isEmpty()) {
            throw new RuntimeException("Execution plan non trouvé: " + execPlanName);
        }
        throw new RuntimeException("Trop d'execution plan trouvés pour: " + execPlanName);
    }


    //getTasksRelatedToJobs

    public void executeTask(String name) throws FacadeException {
        long taskId = execute(new GetTaskIdByNameCommand(name));
        String execRequestId = execute(new RunTaskCommand(taskId));
        GetTaskExecutionStatusCommand taskExecutionStatus = new GetTaskExecutionStatusCommand(execRequestId);
        String status = execute(taskExecutionStatus);
        if (!"ENDED_OK".equals(status)) {
            String log = execute(TaskLogCommand.build(taskId));
            throw new RuntimeException("Execution of job " + name + " failed: " + taskExecutionStatus.getExecutionStatus() + "\n\nTask execution log:\n..." +
                    log.substring(Math.max(0, log.length() - 80 * 15)));
        }
        LOG.info("Job ended successfully: " + taskExecutionStatus.getExecutionStatus());
        String log = execute(TaskLogCommand.build(taskId));
        LOG.info("==================================================================================\n\nTask execution log:\n" + log);
    }

}
