package ch.mno.tatoo.builder;

import ch.mno.tatoo.builder.actions.JobBuilder;
import ch.mno.tatoo.builder.actions.RouteBuilder;
import ch.mno.tatoo.builder.actions.ServiceBuilder;
import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;
import ch.mno.tatoo.facade.commandline.data.Job;
import ch.mno.tatoo.facade.commandline.data.Route;
import ch.mno.tatoo.facade.commandline.data.Service;
import ch.mno.tatoo.facade.common.MessageHandler;
import ch.mno.tatoo.facade.nexus.NexusEntry;
import ch.mno.tatoo.facade.nexus.NexusFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * - check if version exists on Nexus - connect to commandline server Created by dutoitc on 10/08/15.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        System.out.println("  ____________________________________________________");
        System.out.println(" / ");
        System.out.println(" |     Tatoo Builder");
        System.out.println(" \\____________________________________________________\n");
        if (args.length < 2) {
            System.err.println("Wrong number of arguments: " + args.length);
            System.err.println("Syntaxe: [context.properties] [publishedVersion] [ignoreVersionCheck]? [regex=...]?");
            throw new RuntimeException("Incorrect number of arguments: " + args.length);
        }

        // Read args
        Context context = new Context(args[0]);
        String publishedVersion = args[1];
        boolean ignoreVersionCheck = args.length > 2 && "ignoreVersionCheck".equals(args[2]);

        // Read regex
        String regex=".*";
        for (String s: args) {
            if (s.startsWith("regex=")) {
                regex=s.substring(6);
            }
        }

        // Init
        ensureVersionDoesNotAlreadyExist(context, publishedVersion, ignoreVersionCheck);
        Report report = new Report();


        try (TalendCommandLineWrapper cmdline = new TalendCommandLineWrapper(context.getCommandlineServerURL(), context.getCommandlineServerPort()))
        {
            build(context, publishedVersion, report, cmdline, regex);

            System.out.println("===================================================");
            report.report(System.out);
            if (report.hasError()) {
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void build(Context context, String publishedVersion, Report report, TalendCommandLineWrapper cmdline, String regex) throws Exception {
        SVNVersionsFinder svnVersionsFinder = new SVNVersionsFinder(context);
        List<NexusEntry> nexusEntries = NexusFacade.findNexusEntries(context.getNexusURL()+"/content/repositories/releases");
        MessageHandler mh = (type, content) -> System.out.println("Message " + type + ": " + content);
        System.out.println("===============================================");
        System.out.println("Found nexus entries: ");
        //nexusEntries.forEach(n->System.out.println("  - '"+n.getArtifactId()+"' '" + n.getRelease()+"'"));
        System.out.println("===============================================");

        // Connect to TAC and login
        cmdline.connect(context.getTacURL(), context.getTacUsername(), context.getTacPassword());
//        cmdline.loginProject(context.getProject(), context.getSvnUsername(), context.getSvnPassword());
        cmdline.loginProject(context.getProject(), context.getTacUsername(), context.getTacPassword());


        // Build jobs
        JobBuilder jobBuilder = new JobBuilder(context, publishedVersion, cmdline);
        List<Job> jobsToDeploy = svnVersionsFinder.findJobsInMajorVersion(context.getSourcePattern(), cmdline, mh);
        jobsToDeploy = jobsToDeploy.stream()
                .filter(o->Pattern.compile(regex).matcher(o.getFullName()).find() || Pattern.compile(regex).matcher(o.getName()).find())
                .collect(Collectors.toList());
        for (Job job : jobsToDeploy) {
            if (!shouldDeployJob(job)) {
                LOG.info("Skipping job which is not to be deployed: " + job.getName());
                continue;
            }

            if (nexusEntries.stream().filter(n->n.getArtifactId().equals(job.getName()) && n.getRelease().equals(publishedVersion)).count()>0) {
                report.addError("Ignoring job already on Nexus: " + job.getName());
            } else {
                jobBuilder.execute(job, report);
            }
        }

        // Build services
        ServiceBuilder serviceBuilder = new ServiceBuilder(context, publishedVersion, cmdline);
        List<Service> servicesToDeploy = svnVersionsFinder.findServicesInMajorVersion(context.getSourcePattern(), cmdline, mh);
        servicesToDeploy = servicesToDeploy.stream()
                .filter(o->Pattern.compile(regex).matcher(o.getFullName()).find() || Pattern.compile(regex).matcher(o.getName()).find())
                .collect(Collectors.toList());
        for (Service service : servicesToDeploy) {
            if (nexusEntries.stream().filter(n->n.getArtifactId().equals(service.getName()) && n.getRelease().equals(publishedVersion)).count()>0) {
                report.addError("Ignoring service already on Nexus: " + service.getName());
            } else {
                serviceBuilder.execute(service, report);
            }
        }

        // Build routes
        RouteBuilder routeBuilder = new RouteBuilder(context, publishedVersion, cmdline);
        List<Route> routesToDeploy = svnVersionsFinder.findRoutesInMajorVersion(context.getSourcePattern(), cmdline);
        routesToDeploy = routesToDeploy.stream()
                .filter(o->Pattern.compile(regex).matcher(o.getFullName()).find() || Pattern.compile(regex).matcher(o.getName()).find())
                .collect(Collectors.toList());
        for (Route route : routesToDeploy) {
            if (nexusEntries.stream().filter(n->n.getArtifactId().equals(route.getName()) && n.getRelease().equals(publishedVersion)).count()>0) {
                report.addError("Ignoring route already on Nexus: " + route.getName());
            } else {
                routeBuilder.execute(route, report);
            }
        }
    }


    private static boolean shouldDeployJob(Job job) {
        return job.getName().startsWith("SOCLE") || job.getName().startsWith("PREDEPLOY") || job.getName().startsWith("POSTDEPLOY") || job.getName().startsWith("JOB_") ||
                job.getName().startsWith("INFRA") || job.getName().startsWith("WS");
    }

    /**
     * Check on Nexus if (publishedVersion) exists, or skip if ignoreVersionCheck==true
     */
    private static void ensureVersionDoesNotAlreadyExist(Context context, String publishedVersion, boolean ignoreVersionCheck) throws InterruptedException, IOException,
            URISyntaxException {
        LOG.info("Checking on Nexus if version " + publishedVersion + " is already used...");
        if (ignoreVersionCheck) {
            LOG.info("  ... skipping version check (argument ignoreVersionCheck specified)");
        } else {
            if (NexusFacade.isVersionUsed(context.getNexusURL(), publishedVersion)) {
                throw new RuntimeException("ERROR: Version already used: " + publishedVersion + ", cannot continue.");
            }
        }
        LOG.info("   Checked ok.");
    }


}