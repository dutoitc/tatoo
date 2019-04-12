package ch.mno.tatoo.deployer;

import ch.mno.tatoo.deployer.actions.ActionException;
import ch.mno.tatoo.deployer.actions.CleanESBTaskAction;
import ch.mno.tatoo.deployer.actions.SaveESBTaskAction;
import ch.mno.tatoo.deployer.actions.SaveTaskAction;
import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.common.reporters.ConsoleReporter;
import ch.mno.tatoo.facade.common.FacadeException;
import ch.mno.tatoo.facade.karaf.KarafFacade;
import ch.mno.tatoo.facade.karaf.KarafSSHFacade;
import ch.mno.tatoo.facade.karaf.commands.GenericCommand;
import ch.mno.tatoo.facade.karaf.data.KarafElement;
import ch.mno.tatoo.facade.nexus.NexusEntry;
import ch.mno.tatoo.facade.nexus.NexusFacade;
import ch.mno.tatoo.facade.tac.CallException;
import ch.mno.tatoo.facade.tac.TACFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dutoitc on 17/08/15.
 */
public class DeployerMain implements AutoCloseable {

    private static Logger LOG = LoggerFactory.getLogger(DeployerMain.class);
    private final RuntimeProperties runtimeProperties;

    private TACFacade tacFacade;
    private KarafFacade karafFacade;
    private Report report;

    public DeployerMain(RuntimeProperties runtimeProperties) throws IOException {
        this.runtimeProperties = runtimeProperties;
        report = new Report();
        tacFacade = new TACFacade(runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_URL),
                runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_USERNAME),
                runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_PASSWORD),
                runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_EMAIL));
        karafFacade = new KarafSSHFacade(
                runtimeProperties.get(RuntimeProperties.PROPERTIES.KARAF_HOSTNAME),
                runtimeProperties.getInt(RuntimeProperties.PROPERTIES.KARAF_PORT),
                runtimeProperties.get(RuntimeProperties.PROPERTIES.KARAF_USERNAME),
                runtimeProperties.get(RuntimeProperties.PROPERTIES.KARAF_PASSWORD));
    }


    /**
     * Clean every ESBTask of TAC
     */
    @Deprecated // Utiliser tatoo-cli
    private void doModeCleanup(RuntimeProperties runtimeProperties) throws Exception {
        CleanESBTaskAction cleanAction = new CleanESBTaskAction(tacFacade, karafFacade, runtimeProperties.isDryRun());

        // Clean TAC
        tacFacade.getESBTasks().forEach(t -> {
            try {
                cleanAction.execute(t, report);
            } catch (ActionException e) {
                LOG.error("Erreur au cleanup: " + e.getMessage(), e);
            }
        });

        // Clean Karaf
        List<KarafElement> list = karafFacade.findKarafElement().stream()
                .filter(f->f.isBaseGroupId(runtimeProperties.get(RuntimeProperties.PROPERTIES.APPLICATION_GROUPID)))
                .collect(Collectors.toList());
        if(runtimeProperties.isDryRun()) {
            LOG.info("   dry-run: Karaf clean would have cleaned " + list.size());
        } else {
            LOG.info("   Karaf clean of " + list.size() + " bundles");
            String ids = list.stream()
                    .peek(e -> report.addKarafClean(e.getLocation()))
                    .map(e -> e.getId())
                    .collect(Collectors.joining(" "));
            karafFacade.execute(new GenericCommand("uninstall " + ids));
        }

        // Clean Karaf features
        //karafFacade.findKarafFeatures()



        // Clean tasks
        // TODO: ajouter plus de données au rapport (exceptions)
        tacFacade.getTasksRelatedToJobs().forEach(t->{
			try {
                tacFacade.deleteTask(t.getId(), true);
                report.addKarafClean(t.getApplicationName());
            } catch (FacadeException e) {
                e.printStackTrace();
            }
		});

    }

    /**
     * Deploy every ESBTask of Nexus for a given version
     */
    private void doModeDeploy(RuntimeProperties runtimeProperties, String version) throws FacadeException {
        String includesStr = runtimeProperties.get("includes");
        if (includesStr==null) {throw new RuntimeException("Missing 'includes'");}
        String excludesStr = runtimeProperties.get("includes");
        if (excludesStr==null) {throw new RuntimeException("Missing 'excludes'");}
        List<String> includesLst = Arrays.asList(includesStr.split(","));
        List<String> excludesLst = Arrays.asList(excludesStr.split(","));
        List<NexusEntry> entries = findNexusEntries(includesLst, excludesLst, version);
        String tacServerLabel = runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_LABEL);

        // Deploy using TAC
        for (NexusEntry entry : entries) {
            if (entry.getGroupId().contains("service") || entry.getGroupId().contains("route") || entry.getGroupId().contains("WS_")) {
                LOG.info("Deploying " + entry.getArtifactId());
                try {
                    runCleanESBTaskAction(entry);
                    runDeployESBTaskAction(tacFacade, entry, tacServerLabel);
                } catch (Exception e) {
                    LOG.warn("Cannot deploy " + entry.getGroupId() + ":" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
                }
            } else if (entry.getGroupId().contains(".job.")) {
                LOG.info("Deploying job " + entry.getArtifactId());
                try {
                    runDeployJobTaskAction(entry, tacServerLabel);
                } catch (Exception e) {
                    LOG.warn("Cannot deploy " + entry.getGroupId() + ":" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage());
                }
            } else {
                LOG.info("Skipping " + entry.getArtifactId());
            }
        }
    }


    /**
     * Find all Nexus entries for a given version
     * @param version
     * @param includes
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    private List<NexusEntry> findNexusEntries(List<String> includes, List<String> excludes, String version) throws FacadeException {
        // Identification des sources sur Nexus
        String nexusURL = runtimeProperties.get(RuntimeProperties.PROPERTIES.NEXUS_URL);
        LOG.info("Nexus source identifications on " + nexusURL);
        List<NexusEntry> entries = NexusFacade.findNexusEntries(nexusURL);
        LOG.info("... found " + entries.size());

        // Criblage des sources (entries)
        SourceFilterHelper.filterSource(includes, excludes, version, entries);
        LOG.info("... found " + entries.size() + " filtered entries");
        return entries;
    }

    /**
     * Deploy a route+service on TAC (not on Karaf)
     * @param tacFacade
     * @param entry
     * @param serverLabel
     */
    private void runDeployESBTaskAction(TACFacade tacFacade, NexusEntry entry, String serverLabel) {
        SaveESBTaskAction action = new SaveESBTaskAction(tacFacade, karafFacade, runtimeProperties.isDryRun());
        try {
            LOG.info("... deploy " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease());
            SaveESBTaskAction.Data data = new SaveESBTaskAction.Data(entry, serverLabel);
            action.execute(data, report);
        } catch (Exception e) {
            if (entry.getGroupId().contains("route")) {
                LOG.error("    Impossible de déploier la route " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage(), e);
            } else {
                LOG.error("    Impossible de déploier le service " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage(), e);
            }
        }
    }

    /**
     * Deploy a job on TAC (not on jobserver)
     * @param entry
     * @param serverLabel
     */
    private void runDeployJobTaskAction(NexusEntry entry, String serverLabel) {
        SaveTaskAction action = new SaveTaskAction(tacFacade, karafFacade, runtimeProperties.isDryRun());
        try {
            LOG.info("... deploy " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease());
            SaveTaskAction.Data data = new SaveTaskAction.Data(entry, serverLabel);
            action.execute(data, report);
        } catch (Exception e) {
            LOG.error("    Impossible de déploier le job " + entry.getGroupId() + " :" + entry.getArtifactId() + ":" + entry.getRelease() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Delete ESBTask on TAC if present and deleteTask features, bundles from Karaf
     */
    private void runCleanESBTaskAction(NexusEntry entry) throws FacadeException {
        CleanESBTaskAction cleanAction = new CleanESBTaskAction(tacFacade, karafFacade, runtimeProperties.isDryRun());
        tacFacade.getESBTasks().stream()
                .filter(t -> t.getApplicationName().equals(entry.getArtifactId()))
                .forEach(t -> {
                    try {
                        cleanAction.execute(t, report);
                    } catch (ActionException e) {
                        LOG.error("Erreur au cleanup: " + e.getMessage(), e);
                    }
                });
    }


    @Override
    public void close() throws Exception {
        report.write(System.out);
        karafFacade.close();
    }


    // ========================================================================================================================


    public static void main(String[] args) throws Exception {
        boolean modeCleanup = args.length == 2 && args[1].equals("cleanup");
        if (args.length != 2 && !modeCleanup) {
            System.err.println("Wrong number of arguments: " + args.length);
            System.err.println("Syntaxe: [xxx.properties] [publishedVersion]");
            System.err.println("         [runtixxxmeProperties.properties] cleanup");
            throw new RuntimeException("Incorrect number of arguments: " + args.length);
        }
        final RuntimeProperties runtimeProperties = new RuntimeProperties(args[0], new ConsoleReporter());
        runtimeProperties.validate();
        // properties.setDryRun(true);


        try (
                DeployerMain deployer = new DeployerMain(runtimeProperties);
        ) {
            String nexusURL = runtimeProperties.get(RuntimeProperties.PROPERTIES.NEXUS_URL);
            String tacURL = runtimeProperties.get(RuntimeProperties.PROPERTIES.TAC_SERVER_URL);
            long t0 = System.currentTimeMillis();
            if (modeCleanup) {
                LOG.info("  .====================================================================================.");
                LOG.info("  | Tatoo deploy: will cleanup envt");

                LOG.info("  | source: " + nexusURL);
                LOG.info("  | target: " + tacURL);
                if (runtimeProperties.isDryRun()) LOG.info("  | DRY_RUN !!! NO EFFECTIVE DEPLOYMENT WILL BE DONE !!!");
                LOG.info("  \\____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.__/\n");

                deployer.doModeCleanup(runtimeProperties);
                LOG.info("Cleanup done in " + (System.currentTimeMillis() - t0) / 1000 + "s.");
            } else {
                final String publishedVersion = args[1];
                LOG.info("  .====================================================================================.");
                LOG.info("  | Tatoo deploy: will deploy routes, services with version=" + publishedVersion + ".x");
                LOG.info("  | source: " + nexusURL);
                LOG.info("  | target: " + tacURL);
                if (runtimeProperties.isDryRun()) LOG.info("  | DRY_RUN !!! NO EFFECTIVE DEPLOYMENT WILL BE DONE !!!");
                LOG.info("  \\____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.____.-^-.__/\n");

                deployer.doModeDeploy(runtimeProperties, publishedVersion);
                LOG.info("Deploy done in " + (System.currentTimeMillis() - t0) / 1000 + "s.");
            }
        }

        LOG.info("\n\nHave a nice day !\n\n");
    }


}
