package ch.mno.tatoo.deployer.actions;

import ch.mno.tatoo.deployer.Report;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.nexus.NexusEntry;
import ch.mno.tatoo.facade.tac.TACFacade;
import ch.mno.tatoo.facade.tac.commands.AbstractCommand;
import ch.mno.tatoo.facade.tac.commands.AssociatePreGeneratedJobCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save Task (job) on TAC
 * Created by dutoitc on 03/10/17.
 */
public class SaveTaskAction extends AbstractAction<SaveTaskAction.Data> {

    private static Logger LOG = LoggerFactory.getLogger(SaveTaskAction.class);

    public SaveTaskAction(TACFacade tacFacade, KarafFacade karafFacade, boolean isDryRun) {
        super(tacFacade, karafFacade, isDryRun);
    }

    @Override
    public void execute(Data data, Report report) throws ActionException {
        LOG.info("... Installing " + data.groupId + ":" + data.artifactId + ":" + data.release);
        try {
            if (isDryRun) {
                LOG.info("    DRY-RUN: tac-associatePreGeneratedJobCommand");
            } else {
                String taskName=data.artifactId;
                AbstractCommand cmd = new AssociatePreGeneratedJobCommand("TODO_actionName", data.artifactId, data.serverLabel, taskName, data.groupId, data.artifactId, data.release);
                tacFacade.execute(cmd);
            }
            report.addJobSaved(data.artifactId);
        } catch (Exception e) {
            LOG.error("    Impossible de déployer le job " + data.groupId + ":" + data.artifactId + ":" + data.release + ": " + e.getMessage(), e);
            report.addInstallError("Erreur au déploiement du job " + data.artifactId + ": " + e.getMessage());
            throw new ActionException("SaveTask: installation impossible de " + data.groupId + ":" + data.artifactId + ":" + data.release + ": " + e.getMessage(), e);
        }
    }

    public static class Data {
        String groupId;
        String artifactId;
        String release;
        String serverLabel;

        public Data(NexusEntry entry, String serverLabel) {
            groupId = entry.getGroupId();
            artifactId = entry.getArtifactId();
            release = entry.getRelease();
            this.serverLabel = serverLabel;
        }
    }


}
