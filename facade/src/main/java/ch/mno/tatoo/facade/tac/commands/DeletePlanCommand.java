package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 *
 * Created by dutoitc on 12/08/15.
 */
public class DeletePlanCommand extends AbstractCommand<Void> {

    private boolean deleteAllRelatedData;
    private long planId;

    public DeletePlanCommand(boolean deleteAllRelatedData, long planId) {
        super("deletePlan");
        this.deleteAllRelatedData=deleteAllRelatedData;
        this.planId=planId;
    }

    @Override
    public void completeObject(JSONObject obj) {
        super.completeObject(obj);
        obj.put("deleteAllRelatedData", deleteAllRelatedData?"true":"false");
        obj.put("planId",""+planId);
    }


    @Override
    public Void getData() {
        return null;
    }

    @Override
    public void keepResults(JSONObject result) {
        System.out.println("DeleteTaskCommand Result: " + result);
    }



    @Override
    public String toString() {
        return "DeletePlanCommand[]";
    }


}
