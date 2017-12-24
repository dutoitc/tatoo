package ch.mno.tatoo.deployer.actions;

import ch.mno.tatoo.deployer.Report;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.tac.TACFacade;
import ch.mno.tatoo.facade.tac.data.ESBTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clean ESBTask from TAC and Karaf
 * Created by dutoitc on 03/10/17.
 */
public class CleanESBTaskAction extends AbstractAction<ESBTask> {

    private static Logger LOG = LoggerFactory.getLogger(CleanESBTaskAction.class);

    public CleanESBTaskAction(TACFacade tacFacade, KarafFacade karafFacade, boolean isDryRun) {
        super(tacFacade, karafFacade, isDryRun);
    }

    @Override
    public void execute(ESBTask task, Report report) throws ActionException {
        LOG.info("... cleaning EsbTask " + task.getLabel() + " " + task.getApplicationVersion());
        try {
            if (isDryRun) {
                LOG.info("    DRY-RUN: tac-deleteTask #" + task.getId());
            } else {
                tacFacade.deleteTask(task.getId(), false);
            }
            karafFacade.clean(isDryRun, task.getLabel());
            report.addEsbTaskDeleted(task.getLabel());
        } catch (Exception e) {
            LOG.error("    Impossible d'effacer la tâche " + task.getId() + "(" + task.getLabel() + "): " + e.getMessage(), e);
            throw new ActionException("CleanESBTask: clean impossible de " + task.getId() + "(" + task.getLabel() + "): " + e.getMessage(), e);
        }
    }


}
