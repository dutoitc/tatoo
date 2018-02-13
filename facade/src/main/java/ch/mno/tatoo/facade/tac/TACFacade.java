package ch.mno.tatoo.facade.tac;

import ch.mno.tatoo.common.reporters.Reporter;
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


    public <R> R execute(AbstractCommand command) throws URISyntaxException, IOException, CallException {
        long t0 = System.currentTimeMillis();
        LOG.info("Running " + command.toString());

        // Build URL from JSON request
        String json = command.buildJSON(tacUsername, tacPassword, tacEmail);
        String resultStr = facade.call(json);
        JSONObject result = (JSONObject) JSONValue.parse(resultStr);

        // Check return code
        RETURN_CODES returnCode = RETURN_CODES.fromCode((Long) result.get("returnCode"));
        if (returnCode != RETURN_CODES.SUCCESS) {
            StringBuilder sb = new StringBuilder();
            sb.append(result.get("returnCode")).append(":").append(result.get("error"));
            LOG.info("Execution done. " + command.toString() + " in " + (System.currentTimeMillis() - t0) / 1000 + "s");
            LOG.error("Command failed:\n" + json);
            throw new CallException(sb.toString(), command);
        }

        command.keepResults(result);
        LOG.info("Execution done. " + command.toString() + " in " + (System.currentTimeMillis() - t0) / 1000 + "s");
        return (R) command.getData();
    }

    public void deleteTask(int id, boolean deleteRelatedData) throws CallException, IOException, URISyntaxException {
        DeleteTaskCommand command = new DeleteTaskCommand(id, deleteRelatedData);
        execute(command);
    }

    public List<JobTask> getTasks() throws CallException, IOException, URISyntaxException {
        ListTasksCommand command = new ListTasksCommand();
        execute(command);
        return command.getJobTasks();
    }

    public List<ESBTask> getESBTasks() throws CallException, IOException, URISyntaxException {
        ListESBTasksCommand command = new ListESBTasksCommand();
        execute(command);
        return command.getJobTasks();
    }

    public List<JobTask> getTasksRelatedToJobs() throws CallException, IOException, URISyntaxException {
        GetTasksRelatedToJobsCommand command = new GetTasksRelatedToJobsCommand();
        execute(command);
        return command.getTasks();
    }

    public void listTasks() throws CallException, IOException, URISyntaxException {
        ListTasksCommand command = new ListTasksCommand();
        execute(command);
        for (JobTask jobTask : command.getJobTasks()) {
            System.out.println(jobTask.toTableString());
        }
    }


    /**
     * e.g. "INFRA_Service_Ping-feature", "666.0", "G_Technique"
     */
    public String saveESBService(String name, String version, String path, String serverLabel) throws CallException, IOException, URISyntaxException {
        SaveESBTaskCommand command = SaveESBTaskCommand.buildService(name, version, path, serverLabel);
        execute(command);
        return command.getStringResult();
    }


    /**
     * e.g. "INFRA_Route_Ping-feature", "666.0", "G_Technique"
     */
    public String saveESBRoute(String name, String version, String path, String serverLabel) throws CallException, IOException, URISyntaxException {
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

    private Stream<JobTask> findTasks(String regex) throws CallException, IOException, URISyntaxException {
        List<JobTask> tasks = execute(new ListTasksCommand());
        return tasks.stream()
                .peek(j->System.out.println("Checking "+j.getApplicationName()+" for " + regex))
                .filter(j -> Pattern.compile(regex).matcher(j.getApplicationName()).find());
    }


    private Stream<ESBTask> findEsbTasks(String regex) throws CallException, IOException, URISyntaxException {
        List<ESBTask> tasks = execute(new ListESBTasksCommand());
        return tasks.stream()
//                .peek(j->System.out.println("Checking "+j.getApplicationName()+" for " + regex))
                .filter(j -> Pattern.compile(regex).matcher(j.getApplicationName()).find());
    }


    /**
     * Deploy Task (job)
     */
    public void deployTasks(String regex, Reporter reporter) throws CallException, IOException, URISyntaxException {
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
    public void deployEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws CallException, IOException, URISyntaxException {
        findEsbTasks(regex)
//                .peek(t->System.out.println("Check " + t.getApplicationType()+", "+(t.getApplicationType()== FEATURE_TYPE.SERVICE)))
                .filter(t->(t.getApplicationType()== FEATURE_TYPE.SERVICE && services) || (t.getApplicationType()== FEATURE_TYPE.ROUTE && routes))
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
    public void startEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws CallException, IOException, URISyntaxException {
        findEsbTasks(regex)
                .filter(t->(t.getApplicationType()== FEATURE_TYPE.SERVICE && services) || (t.getApplicationType()== FEATURE_TYPE.ROUTE && routes))
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
    public void stopEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws CallException, IOException, URISyntaxException {
        findEsbTasks(regex)
                .filter(t->(t.getApplicationType()== FEATURE_TYPE.SERVICE && services) || (t.getApplicationType()== FEATURE_TYPE.ROUTE && routes))
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


    public void undeployEsbTasks(String regex, Reporter reporter, boolean services, boolean routes) throws CallException, IOException, URISyntaxException {
        findEsbTasks(regex)
                .filter(t->(t.getApplicationType()== FEATURE_TYPE.SERVICE && services) || (t.getApplicationType()== FEATURE_TYPE.ROUTE && routes))
                .peek(c -> reporter.logInfo("Undeploying ESBTask " + c.getLabel() + " " + c.getApplicationVersion()))
                .forEach(c -> {
                    try {
                        execute(new RequestUndeployEsbTaskCommand(String.valueOf(c.getId())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        reporter.logError("Erreur: " + e.getMessage());
                    }
                });
    }

    /**
     * DaysOfMonth, DaysOfWeek: exclusifs. Choisir "*" si inutile. DaysOfWeek: 1=dimanche
     */
    public void createCronTrigger(String taskId, String label, String description, String hours, String minutes, String daysOfMonth, String daysOfWeek, String months, String years) throws CallException, IOException, URISyntaxException {
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

    public long getTaskIdByName(String taskName) throws URISyntaxException, IOException, CallException {
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

    public long getExecPlanIdByName(String execPlanName) throws URISyntaxException, IOException, CallException {
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

    public static void main(String[] args) throws IOException, URISyntaxException, CallException {
        //tacdev/administrator
//		TACFacade facade = new TACFacade("http://server:56751/tacdev", "username", "password", "mail");
//        TACFacade facade = new TACFacade("http://hostname:56751/tacdev", "username", "password", "mail");
		TACFacade facade = new TACFacade("http://hostname:56751/tacdev", "username", "password", "mail");


//        facade.createCronTrigger(""+facade.getExecPlanIdByName("test1"), "test label", "some desc", "12,13", "20,21", "3,2", "8,12", "2017,2018");

//        facade.createCronTrigger("" + facade.getTaskIdByName("INFRA_Job_Ping"), "test label3", "some desc", "12,13", "20,21", "*", "1", "8,12", "2017,2018");
        facade.findEsbTasks(".*")
                .forEach(t->System.out.println(t.getApplicationName() + " " + t.getApplicationFeatureURL()));


//        ListExecutionPlansCommand command = new ListExecutionPlansCommand();
//        facade.execute(command);
//        for (ExecutionPlan plan: command.getExecutionPlanList()) {
//            System.out.println("Found plan " + plan.getPlanId() + ": " + plan.getLabel() + " " + plan.getPlanPrms());
//        }


//		ListExecutionPlansCommand command = new ListExecutionPlansCommand();
//		facade.execute(command);
//		for (ExecutionPlan plan: command.getExecutionPlanList()) {
//			System.out.println("Found plan " + plan.getPlanId() + ": " + plan.getLabel());
//			facade.execute(new DeletePlanCommand(true, plan.getPlanId()));
//			// TODO: Test me
//		}


		/*ListESBTasksCommand command = new ListESBTasksCommand();
        facade.execute(command);
		command.getExecutionPlanList().stream()
				.peek(c->System.out.println("Deploying " + c.getLabel() + " " + c.getApplicationVersion()))
				.forEach(c -> {
					try {
						facade.execute(new RequestDeployEsbTaskCommand(String.valueOf(c.getId())));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (CallException e) {
						e.printStackTrace();
					}
				});*/


//		facade.execute(new RequestDeployEsbTaskCommand("916"));


        // FIXME: seulement les tâches Tatoo ?
//		facade.getTasksRelatedToJobs().forEach(t->{
//			try {
//				facade.deleteTask(t.getId(), true);
//			} catch (CallException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//		});


        //facade.deleteTask(435, true);


//		String taskName="INFRA_Job_Ping";
//		AbstractCommand cmd = new AssociatePreGeneratedJobCommand("TST_actionName", "TST_Description", "serv1", taskName, "ch.mno.dummyapp.job.G_Technique", "INFRA_Job_Ping", "666.4");
//		facade.execute(cmd);

//		AbstractCommand cmd = new CreateTaskCommand("INFRA_Job_Ping", "666.4", "serv1");
//		facade.execute(cmd);




		/*for (ESBTask task: facade.getESBTasks()) {
            if ( task.getLabel().startsWith("INFRA_Route_Ping")) {
				facade.deleteTask(task.getId(), true);
			}
		}
		facade.execute(SaveESBTaskCommand.buildRoute("INFRA_Route_Ping-feature", "666.4", "G_Technique", "serv1"));
*/

//		for (JobTask task: facade.getTasks()) {
//			if ( task.getLabel().startsWith("INFRA_Job_Ping")) {
//				facade.deleteTask(task.getId(), true);
//			}
//		}
//		facade.execute(SaveESBTaskCommand.buildJob("INFRA_Job_Ping", "17.13.0", "G_Technique", "1.0", "serv1"));
//		facade.execute(new CreateTaskCommand("INFRA_Job_Ping", "17.13.0", "INFRA_Job_Ping"));
//		facade.execute(new RequestDeployTaskCommand("-1"));

        // TODO: execution task


        // https://docs.tibco.com/pub/js-etl/6.2.1/doc/pdf/Talend_AdministrationCenter_UG_6.2.1_EN.pdf

        // CreateTask
//		facade.listTasks();
//		facade.execute(new CreateTaskCommand("INFRA_Job_Ping", "666.0", "INFRA_Job_Ping"));
//		facade.execute(new RequestDeployTaskCommand("-1"));

//		facade.listTasks();

        //facade.execute(new GetTasksRelatedToJobsCommand());
//		facade.execute(new HelpCommand());

        //facade.execute(new CreateTaskCommand("INFRA_Job_Ping-feature", "666.0"));

        //facade.execute(SaveESBTaskCommand.buildService("INFRA_Service_Ping-feature", "666.0", "G_Technique"));
        //facade.execute(SaveESBTaskCommand.buildRoute("INFRA_Route_Ping-feature", "666.0", "G_Technique"));
        // KO facade.execute(SaveESBTaskCommand.buildJob("INFRA_Job_Ping", "666.0", "G_Technique", "1.0"));
        // TODO: try job creation by Karaf + saveESBTask ?

        //facade.execute(new HelpCommand("saveESBTask"));

        //facade.listTasks();
        //facade.execute(new DeleteTaskCommand("962", true));
//		facade.execute(new DeleteTaskCommand("963", true));
//		facade.execute(new DeleteTaskCommand("964", true));
//		facade.execute(new DeleteTaskCommand("966", true));

        //facade.execute(new DeleteTaskCommand("976", true));


        //7|0|14|http://server:56751/tacdev/administrator/|7F2676E53FB6437EDA89DC664C4D20EA|org.talend.gwtadministrator.client.module.conductor.common.service.Jo
        // bConductorService|saveExecutionTask|org.talend.gwtadministrator.client.module.conductor.common.model.ExecutionTaskBean/285717412|
        // INFRA_Service_Ping-feature|ROUTE|666.0|Default|JOB_SERVER_1|mvn:ch.mno.dummyapp.service.G_Technique/INFRA_Service_Ping-feature/666.0/xml|test6|INFRA_Service_Ping|releases|1|2|3|4|1|5|5|0|0|0|0|6|7|8|0|0|0|0|0|0|9|0|0|0|10|11|0|0|0|0|0|0|0|1

        // http://businessintelligence.neowp.fr/talend-metaservlet/
        //facade.execute(new GenericCommand("help saveESBTask"));


        // listTasks: SOCLE, PRE, POST
        // listESBTasks: SERVICE, ROUTE

        // saveESBTask

        // requestDeploy
        // requestPauseTriggers
        // requestResumeTriggers
        //   listExecutionPlans
        //   listConnection
        //   importExecutionPlan
        // getTaskStatus
        // getTaskIdByName
        // getTaskExecutionStatus
        //   getArchivaUrl
        // createTask
        // associatePreGeneratedJob

        //facade.execute(SaveESBTaskCommand.buildJob("INFRA_Job_Ping", "666.0", "G_Technique", "1.0"));
    }


}
