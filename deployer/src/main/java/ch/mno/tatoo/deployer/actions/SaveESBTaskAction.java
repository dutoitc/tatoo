package ch.mno.tatoo.deployer.actions;

import ch.mno.tatoo.deployer.Report;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.nexus.NexusEntry;
import ch.mno.tatoo.facade.tac.TACFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save ESBTask from TAC and Karaf (service, route)
 * Created by dutoitc on 03/10/17.
 */
public class SaveESBTaskAction extends AbstractAction<SaveESBTaskAction.Data> {

    private static Logger LOG = LoggerFactory.getLogger(SaveESBTaskAction.class);

    public SaveESBTaskAction(TACFacade tacFacade, KarafFacade karafFacade, boolean isDryRun) {
        super(tacFacade, karafFacade, isDryRun);
    }

    @Override
    public void execute(Data data, Report report) throws ActionException {
        LOG.info("... Installation de " + data.groupId + ":" + data.artifactId + ":" + data.release);
        try {
            if (data.groupId.contains("service") || data.groupId.contains("WS_")) {
                if (isDryRun) {
                    LOG.info("    DRY-RUN: tac-saveESBService");
                } else {
                    LOG.info("... " + tacFacade.saveESBService(data.artifactId, data.release, data.path, data.serverLabel));
                }
                report.addServiceSaved(data.artifactId);
            } // Deploy route
            else if (data.groupId.contains("route")) {
                if (isDryRun) {
                    LOG.info("    DRY-RUN: tac-saveESBRoute");
                } else {
                    LOG.info("... " + tacFacade.saveESBRoute(data.artifactId, data.release, data.path, data.serverLabel));
                }
                report.addRouteSaved(data.artifactId);
            } else {
                report.addInstallError("Erreur à l'installation de " + data.artifactId + "(groupId non supporté)");
                throw new RuntimeException("Unsupported groupId: " + data.groupId);
            }
        } catch (Exception e) {
            LOG.error("    Impossible de sauver la tâche " + data.groupId + ":" + data.artifactId + ":" + data.release + ": " + e.getMessage(), e);
            report.addInstallError("Erreur à l'installation de la route ou service " + data.artifactId + ": " + e.getMessage());
            throw new ActionException("DeployESBTask: deploy impossible de " + data.groupId + ":" + data.artifactId + ":" + data.release + ": " + e.getMessage(), e);
        }
    }

    public static class Data {
        String groupId;
        String artifactId;
        String release;
        String path;
        String serverLabel;

        public Data(NexusEntry entry, String serverLabel) {
            groupId = entry.getGroupId();
            artifactId = entry.getArtifactId();
            release = entry.getRelease();
            this.serverLabel = serverLabel;

            // Get studio path: remove package prefix (added on NexusReleaser)
            path = entry.getGroupId().replace("ch.mno.tatoo.service.", "").replace("ch.mno.tatoo.route.", "");
        }
    }


}
