package ch.mno.tatoo.facade.tac.commands;

import ch.mno.tatoo.facade.tac.data.ExecutionPlan;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * List execution plans
 * Created by dutoitc on 12/08/15.
 */
public class ListExecutionPlansCommand extends AbstractCommand<List<ExecutionPlan>> {

    private List<ExecutionPlan> executionPlanList;


    public ListExecutionPlansCommand() {
        super("listExecutionPlans");
    }


    @Override
    public List<ExecutionPlan> getData() {
        return executionPlanList;
    }

    @Override
    public void keepResults(JSONObject result) {
        JSONObject results = (JSONObject) result.get("result");

        executionPlanList = new ArrayList<>(results.size());
        for (Object row :results.values())  {
            JSONArray values = (JSONArray) row;
            if (values.size()==0) {
                return;
            }

            for (int i=0; i<values.size(); i++) {
                JSONObject row2 = (JSONObject) values.get(i); // TODO: if values are > 1 ?...

                ExecutionPlan plan = new ExecutionPlan();
                plan.setExecPlanTimeOut((String) row2.get("execPlanTimeout"));
                plan.setIdQuartzJob((String) row2.get("quartzJob"));
                plan.setLabel((String) row2.get("label"));
                plan.setPlanId((Long) row2.get("planId"));
                //plan.setPlanPrms((String) row2.get("planPrms"));
                plan.setStatus((String) row2.get("status"));
                plan.setJobs(new ArrayList<>(extractJobs((JSONObject) row2.get("planParts"))));
                executionPlanList.add(plan);
            }

//            row2.get("planParts")

            //System.out.println(plan.getPlanId() + " " + plan.getLabel()+ " " + plan.getJobs());

//            ESBTask data = new ESBTask();
//            data.setId(Integer.parseInt(row.get("id").toString()));
//            data.setApplicationVersion(safeString(row.get("applicationVersion")));
//            data.setJobServerLabelHost(safeString(row.get("jobServerLabelHost")));
//            data.setApplicationFeatureURL(safeString(row.get("applicationFeatureURL")));
//            data.setApplicationName(safeString(row.get("applicationName")));
//            data.setPid(safeString(row.get("pid")));
//            data.setLabel(safeString(row.get("label")));
//            data.setApplicationType(FEATURE_TYPE.valueOf(row.get("applicationType").toString()));
//            data.setRepositoryName(safeString(row.get("repositoryName")));
//            data.setContextName(safeString(row.get("contextName")));
//            executionPlanList.add(data);
        }

    }

    private Set<String> extractJobs(JSONObject planParts) {
        Set<String> jobs = new LinkedHashSet<>();
        Object planPartTaskId = planParts.get("planPartTaskId");
        if (planPartTaskId==null) return jobs; // no jobs
        jobs.add(planPartTaskId.toString());

        JSONArray childParts = (JSONArray)planParts.get("childParts");
        if (childParts!=null) {
            for (int i = 0; i < childParts.size(); i++) {
                jobs.addAll(extractJobs((JSONObject) childParts.get(i)));
            }
        }

        return jobs;
    }

    public List<ExecutionPlan> getExecutionPlanList() {
        return executionPlanList;
    }


    private String safeString(Object value) {
        if (value == null) return "";
        return value.toString();
    }


    @Override
    public String toString() {
        return "ListExecutionPlansCommand[]";
    }


}
