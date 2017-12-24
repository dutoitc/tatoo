package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.deployer.Report;
import ch.mno.tatoo.deployer.SourceFilterHelper;
import ch.mno.tatoo.deployer.actions.SaveESBTaskAction;
import ch.mno.tatoo.deployer.actions.SaveTaskAction;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.nexus.NexusEntry;
import ch.mno.tatoo.facade.nexus.NexusFacade;
import ch.mno.tatoo.facade.tac.TACFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 23/11/17.
 */
public class InstallCommand extends AbstractCommand {

    private Pattern regex;

    public void postInit() {
        super.postInit();
        //
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("install [version] [regex]", "installe les jobs, routes, services sans les déployer (version: YY.RI, ex. 18.12)")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("install")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("install")) {
            if (args.size() < 2) throw new RuntimeException("La version est obligatoire");
            if (args.size()>2) {
                regex = Pattern.compile(args.get(2));
            }
            try {
                install(args.get(1));
            } catch (Exception e) {
                reporter.logError("Erreur à l'install: " + e.getMessage());
            }
        }
    }

    private void install(String version) throws Exception {
        Report report = new Report();
        TACFacade tacFacade = buildTACFacade();
        KarafFacade karafFacade = buildKarafFacade();
        String tacServerLabel = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_LABEL);
        String nexusURL = properties.get(RuntimeProperties.PROPERTIES.NEXUS_URL);

        // Find nexus versions
        List<String> includesLst = properties.getIncludes();
        List<String> excludesLst = properties.getExcludes();
        List<NexusEntry> entries = findNexusEntries(includesLst, excludesLst, version);

        // Deploy using TAC
        for (NexusEntry entry : entries) {
            if (regex!=null && (!regex.matcher(entry.getGroupId()).find() && !regex.matcher(entry.getArtifactId()).find()))  {
                reporter.logTrace("Déploiement ignoré (regex) de " + entry.getGroupId()+"/"+entry.getArtifactId());
                continue;
            }

            if (entry.getGroupId().contains("service") || entry.getGroupId().contains("route") || entry.getGroupId().contains("WS_")) {
                if (properties.isDryRun()) {
                    reporter.logInfo("DRY-Mode: Installation de " + entry.getArtifactId());
                } else {
                    reporter.logInfo("Installation de " + entry.getArtifactId());
                    try {
                        runDeployESBTaskAction(tacFacade, karafFacade, entry, tacServerLabel, report);
                    } catch (Exception e) {
                        reporter.logError("Cannot install " + entry.getGroupId() + ":" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
                    }
                }
            } else if (entry.getGroupId().contains(".job.")) {
                if (properties.isDryRun()) {
                    reporter.logInfo("DRY-Mode: Installation du job " + entry.getArtifactId());
                } else {
                    reporter.logInfo("Installation du job " + entry.getArtifactId());
                    try {
                        runDeployJobTaskAction(tacFacade, karafFacade, entry, tacServerLabel, report);
                    } catch (Exception e) {
                        reporter.logError("Cannot install " + entry.getGroupId() + ":" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
                    }
                }
            } else {
                reporter.logInfo("Skipping " + entry.getArtifactId());
            }
        }

        reporter.logInfo("================[ Résumé ]================");
        reporter.logInfo("Jobs installés: " + report.getJobsSaved().size());
        report.getJobsSaved().stream().map(t->" - "+t).forEach(reporter::logInfo);
        reporter.logInfo("Routes installées: " + report.getRoutesSaved().size());
        report.getRoutesSaved().stream().map(t->" - "+t).forEach(reporter::logInfo);
        reporter.logInfo("Services installés: " + report.getServicesSaved().size());
        report.getServicesSaved().stream().map(t->" - "+t).forEach(reporter::logInfo);
        reporter.logInfo("Erreurs: " + report.getErrors().size());
        report.getErrors().stream().map(t->" - "+t).forEach(reporter::logInfo);
    }

    /**
     * Find all Nexus entries for a given version
     * @param version
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    private List<NexusEntry> findNexusEntries(List<String> includes, List<String> excludes, String version) throws IOException, URISyntaxException, InterruptedException {
        String tacServerLabel = properties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_LABEL);
        String nexusURL = properties.get(RuntimeProperties.PROPERTIES.NEXUS_URL);

        // Identification des sources sur Nexus
        reporter.logInfo("Nexus source identifications on " + nexusURL);
        List<NexusEntry> entries = NexusFacade.findNexusEntries(nexusURL);
        reporter.logInfo("... found " + entries.size());

        // Filtrage des sources (entries)
        SourceFilterHelper.filterSource(includes, excludes, version, entries);
        reporter.logInfo("... found " + entries.size() + " filtered entries");
        return entries;
    }

    /**
     * Deploy a route+service on TAC (not on Karaf)
     * @param entry
     * @param serverLabel
     */
    private void runDeployESBTaskAction(TACFacade tacFacade, KarafFacade karafFacade, NexusEntry entry, String serverLabel, Report report) {
        SaveESBTaskAction action = new SaveESBTaskAction(tacFacade, karafFacade, properties.isDryRun());
        try {
            reporter.logInfo("... deploy " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease());
            SaveESBTaskAction.Data data = new SaveESBTaskAction.Data(entry, serverLabel);
            action.execute(data, report);
        } catch (Exception e) {
            if (entry.getGroupId().contains("route")) {
                reporter.logError("    Impossible de déploier la route " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
            } else {
                reporter.logError("    Impossible de déploier le service " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Deploy a job on TAC (not on jobserver)
     * @param entry
     * @param serverLabel
     */
    private void runDeployJobTaskAction(TACFacade tacFacade, KarafFacade karafFacade, NexusEntry entry, String serverLabel, Report report) {
        SaveTaskAction action = new SaveTaskAction(tacFacade, karafFacade, properties.isDryRun());
        try {
            reporter.logInfo("... deploy " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease());
            SaveTaskAction.Data data = new SaveTaskAction.Data(entry, serverLabel);
            action.execute(data, report);
        } catch (Exception e) {
            reporter.logError("    Impossible de déploier le job " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
        }
    }


}