package ch.mno.tatoo.facade.bonita;

import ch.mno.tatoo.facade.common.FacadeException;
import org.apache.commons.lang.StringUtils;
import org.bonitasoft.engine.api.*;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.FlowNodeType;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstanceSearchDescriptor;
import org.bonitasoft.engine.bpm.process.*;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.search.Order;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.util.APITypeManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by dutoitc on 30/04/18.
 */
public class BonitaFacade {

    private String serverURL;
    private String applicationName;
    private String username;
    private String password;

    public BonitaFacade(String serverURL, String applicationName, String username, String password) {
        this.serverURL = serverURL;
        this.applicationName = applicationName;
        this.username = username;
        this.password = password;
    }


    public SearchResult<ProcessInstance> findAllProcessInstances(int nbResults) throws FacadeException {
        try {
            APISession apiSession = buildAPISession();
            final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            final SearchOptions searchOptions = new SearchOptionsBuilder(0, nbResults).done();
            SearchResult<ProcessInstance> inst = processAPI.searchProcessInstances(searchOptions);
            return inst;
        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    public void deleteProcessInstance(int processInstanceId) throws FacadeException {
        try {
            APISession apiSession = buildAPISession();
            final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            processAPI.deleteProcessInstance(processInstanceId);

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    /**
     * N'efface pas les cas archived
     */
    public void deleteProcessInstances(long processDefinitionId, int nbElements) throws FacadeException {
        try {
            APISession apiSession = buildAPISession();
            final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
//        processAPI.deleteProcessInstances(processDefinitionId, 0, nbElements);
            long nbOfDeletedProcess = 0;
            do {
                nbOfDeletedProcess = processAPI.deleteProcessInstances(processDefinitionId, 0, 100);
                System.out.println("Deleted instances of processDefinitionId=" + processDefinitionId + ": " + nbOfDeletedProcess);
            } while (nbOfDeletedProcess != 0);

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    public long countProcessInstances(long processDefinitionId) throws FacadeException {
        try {
            APISession apiSession = buildAPISession();
            final ProcessAPI processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 1)
                    .filter(HumanTaskInstanceSearchDescriptor.PROCESS_DEFINITION_ID, processDefinitionId).done();
            return processAPI.searchProcessInstances(searchOptions).getCount();
        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }


    /**
     * @param blacklist blacklist patterns, like (TEST|AUDIT).*
     * @return
     * @throws FacadeException
     */
    public List<BonitaProcess> findProcesses(String blacklist) throws FacadeException {
        try {
        APISession apiSession = buildAPISession();
        System.out.println("Looking for Bonita processes on " + serverURL + "/" + applicationName);
        if (blacklist == null || "".equals(blacklist)) {
            blacklist = "DUMMY";
        }

        final ProcessAPI processAPI;
            processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 100).sort(ProcessDeploymentInfoSearchDescriptor.DEPLOYMENT_DATE, Order.DESC).done();
            final SearchResult<ProcessDeploymentInfo> deploymentInfoResults = processAPI.searchProcessDeploymentInfos(searchOptions);

            Pattern patBlacklist = Pattern.compile(blacklist);
            return deploymentInfoResults.getResult().stream()
                    .filter(o -> !patBlacklist.matcher(o.getName()).matches())
                    .map(r -> mapBonitaProcess(r))
                    .collect(Collectors.toList());


        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    private BonitaProcess mapBonitaProcess(ProcessDeploymentInfo r) {
        return new BonitaProcess(r.getName(), r.getVersion(), r.getActivationState().toString(), r.getId(), r.getProcessId());
    }

    public BonitaDeployReport deploy(List<File> barFiles) throws FacadeException {
        BonitaDeployReport report = new BonitaDeployReport();

        APISession apiSession = buildAPISession();
        ProcessAPI processAPI = buildProcessAPI(apiSession);

        // Deploy
        for (File p : barFiles) {
            try {
                BusinessArchive businessArchive = BusinessArchiveFactory.readBusinessArchive(p);
                final ProcessDefinition processDefinition = processAPI.deployAndEnableProcess(businessArchive);
                report.addBarDeployed(findBonitaProcess(processAPI, processDefinition.getId()));
            } catch (BonitaException e) {
                report.addError("BonitaException on deployment of " + p.getName() + ": " + e.getMessage());
            } catch (IOException e) {
                report.addError("IOException on deployment of " + p.getName() + ": " + e.getMessage());
            }
        }

        return report;
    }

    public BonitaUndeployReport undeploy(List<String> names) throws FacadeException {
        if (true) throw new RuntimeException("Not yet implemented (some work to be done around id/processId)");
        BonitaUndeployReport report = new BonitaUndeployReport();

        APISession apiSession = buildAPISession();
        ProcessAPI processAPI = buildProcessAPI(apiSession);

        // Find processes
        Map<String, BonitaProcess> processes = new HashMap<>();
        findProcesses(null).forEach(p -> {
            processes.put(p.getName() + ":" + p.getVersion(), p);
        });

        // Deploy
        for (String name : names) {
            try {
                if (processes.containsKey(name)) {
                    BonitaProcess bonitaProcess = processes.get(name);
                    processAPI.deleteProcessDefinition(bonitaProcess.getId());
                    report.addBarUndeployed(bonitaProcess);
                }
            } catch (BonitaException e) {
                report.addError("BonitaException on deployment of " + name + ": " + e.getMessage());
            }
        }

        return report;
    }


    private BonitaProcess findBonitaProcess(ProcessAPI processAPI, long processDefinitionId) throws ProcessDefinitionNotFoundException {
        return mapBonitaProcess(processAPI.getProcessDeploymentInfo(processDefinitionId));
    }

    private PlatformSession buildPlatformSession() throws FacadeException {
        try {
            Map<String, String> settings = new HashMap<String, String>();
            settings.put("server.url", serverURL);
            settings.put("application.name", applicationName);
            APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);

            // get the LoginAPI using the TenantAPIAccessor
            PlatformLoginAPI platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
            return platformLoginAPI.login(username, password);

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    private APISession buildAPISession() throws FacadeException {
        try {
            Map<String, String> settings = new HashMap<String, String>();
            settings.put("server.url", serverURL);
            settings.put("application.name", applicationName);
            APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);

            // get the LoginAPI using the TenantAPIAccessor
            LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();

            // log in to the tenant to create a session
            return loginAPI.login(username, password);

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    private ProcessAPI buildProcessAPI(APISession apiSession) throws FacadeException {
        final ProcessAPI processAPI;
        try {
            processAPI = TenantAPIAccessor.getProcessAPI(apiSession);

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
        return processAPI;
    }


    public BonitaDeployReport deployFiles(List<String> files) throws FacadeException {
        List<File> filesObj = files.stream().map(File::new).collect(Collectors.toList());
        return deploy(filesObj);
    }


    public long countExecutingNotHumanSince(Date time) throws FacadeException {
        final ProcessAPI processAPI;
        try {
            processAPI = TenantAPIAccessor.getProcessAPI(buildAPISession());

            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 1000000).done();
            SearchResult<ActivityInstance> res = processAPI.searchActivities(searchOptions);

            return res.getResult().stream()
                    .filter(r -> r.getLastUpdateDate() == null || r.getLastUpdateDate().after(time))
                    .filter(r -> r.getType() != FlowNodeType.HUMAN_TASK)
                    .filter(r -> r.getType() != FlowNodeType.USER_TASK)
                    .filter(r -> r.getType() != FlowNodeType.MANUAL_TASK)
                    .filter(r -> "executing".equals(r.getState()))
                    .count();

        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    /**
     * @return
     */
    public BonitaStatsResult statsObject() throws FacadeException {
        // EXECUTING
        //153	 AUTOMATIC_TASK "Contrôle existence dépendances annonce REE"
        APISession apiSession = buildAPISession();

        final ProcessAPI processAPI;
        try {
            processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 1000000).done();//.sort(ActivityInstanceSearchDescriptor..DEPLOYMENT_DATE, Order.DESC).done();
            SearchResult<ActivityInstance> res = processAPI.searchActivities(searchOptions);

            BonitaStatsResult result = new BonitaStatsResult();
            res.getResult().forEach(r -> result.add(r.getType(), r.getName(), r.getState()));
            return result;


        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }


    public String stats() throws FacadeException {
        APISession apiSession = buildAPISession();

        final ProcessAPI processAPI;
        try {
            processAPI = TenantAPIAccessor.getProcessAPI(apiSession);
            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 1000000).done();//.sort(ActivityInstanceSearchDescriptor..DEPLOYMENT_DATE, Order.DESC).done();
            SearchResult<ActivityInstance> res = processAPI.searchActivities(searchOptions);

            Map<String, Map<String, Integer>> count = new HashMap<>();
            res.getResult().stream()
                    .forEach(r -> {
                        String key = StringUtils.left(r.getType().toString(), 16) + " " + r.getName();
                        if (!count.containsKey(r.getState())) {
                            count.put(r.getState(), new HashMap<>());
                        }
                        Map<String, Integer> mapByState = count.get(r.getState());
                        if (mapByState.containsKey(key)) {
                            mapByState.put(key, mapByState.get(key) + 1);
                        } else {
                            mapByState.put(key, 1);
                        }
                    });

            StringBuilder sb = new StringBuilder();
            sb.append("Stats Bonita, count by type d'activité\n");
            count.entrySet().stream().sorted((a, b) -> {
                Integer av = getOrderStatus(a.getKey());
                Integer bv = getOrderStatus(b.getKey());
                return av.compareTo(bv);
            }).forEach(e -> {
                sb.append(e.getKey().toUpperCase()).append('\n');
                e.getValue().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).forEach(f -> {
                    sb.append("- " + f.getValue() + "\t " + f.getKey() + '\n');
                });
            });
            return sb.toString();


        } catch (Exception e) {
            throw new FacadeException("An error occured: " + e.getMessage(), e);
        }
    }

    private Integer getOrderStatus(String a) {
        switch (a) {
            case "ready":
                return 1;
            case "executing":
                return 2;
            case "completed":
                return 3;
            case "failed":
                return 4;
            default:
                return 5;
        }
    }

}