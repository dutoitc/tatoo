package ch.mno.tatoo.builder;

import ch.mno.tatoo.facade.commandline.TalendCommandLineWrapper;
import ch.mno.tatoo.facade.commandline.commands.ListJobCommand;
import ch.mno.tatoo.facade.commandline.commands.ListRouteCommand;
import ch.mno.tatoo.facade.commandline.commands.ListServiceCommand;
import ch.mno.tatoo.facade.commandline.data.Job;
import ch.mno.tatoo.facade.commandline.data.Route;
import ch.mno.tatoo.facade.commandline.data.Service;
import ch.mno.tatoo.facade.common.MessageHandler;
import ch.mno.tatoo.facade.svn.SVNHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 10/09/15.
 */
public class SVNVersionsFinder {

    private Context context;

    public SVNVersionsFinder(Context context) {
        this.context = context;
    }

    /**
     * Find jobs using command-line, then consolidate with latest version read on svn, keep job if it is in a major version
     */
    public List<Job> findJobsInMajorVersion(String pattern, TalendCommandLineWrapper cmdline, MessageHandler mh) throws Exception {
        Pattern compile = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        ListJobCommand command = new ListJobCommand(compile);
        cmdline.execute(command);
        List<Job> jobs = command.extractData();

        List<Job> jobsMajor = new ArrayList<>();
        for (Job job : jobs) {
            if (context.isBlacklisted(job.getFullName())) continue;

            Float latestVersion = SVNHelper.getLatestVersion(job, context.getSvnURL(), context.getSvnUsername(), context.getSvnPassword());
            if (latestVersion != null && latestVersion - Math.floor(latestVersion) < 0.1) {
                jobsMajor.add(job);
                job.setVersion(latestVersion);
//				System.out.println("Found job " + job.getFullName() + " " + latestVersion);
            } else {
                System.out.println("Ignoring job in non-major version: " + job.getFullName() + ":" + latestVersion);
                if (mh != null) {
                    mh.handleMessage("SVN_IGNORING_NON_MAJOR", "Ignoring job in non-major version: " + job.getFullName() + ":" + latestVersion);
                }
            }
        }
        return jobsMajor;
    }


    /**
     * Find services using command-line, then consolidate with latest version read on svn, keep job if it is in a major version
     */
    public List<Service> findServicesInMajorVersion(String pattern, TalendCommandLineWrapper cmdline, MessageHandler mh) throws Exception {
        Pattern compile = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        ListServiceCommand command = new ListServiceCommand(compile);
        cmdline.execute(command);
        List<Service> services = command.extractData();

        List<Service> servicesMajor = new ArrayList<>();
        for (Service service : services) {
            if (context.isBlacklisted(service.getFullName())) continue;

            Float latestVersion = SVNHelper.getLatestVersion(service, context.getSvnURL(), context.getSvnUsername(), context.getSvnPassword());
            if (latestVersion != null && latestVersion - Math.floor(latestVersion) < 0.05) {
                servicesMajor.add(service);
                service.setVersion(latestVersion);
//				System.out.println("Found service " + service.getFullName() + " " + latestVersion);
            } else {
                System.out.println("Ignoring service in non-major version: " + service.getFullName() + ":" + latestVersion);
                mh.handleMessage("SVN_IGNORING_NON_MAJOR", "Ignoring service in non-major version: " + service.getFullName() + ":" + latestVersion);
            }
        }
        return servicesMajor;
    }


    /**
     * Find routes using command-line, then consolidate with latest version read on svn, keep job if it is in a major version
     */
    public List<Route> findRoutesInMajorVersion(String pattern, TalendCommandLineWrapper cmdline) throws Exception {
        ListRouteCommand command = new ListRouteCommand(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        cmdline.execute(command);
        List<Route> routes = command.extractData();

        List<Route> servicesMajor = new ArrayList<>();
        for (Route route : routes) {
            if (context.isBlacklisted(route.getFullName())) continue;

//			Float latestVersion = SVNHelper.getLatestVersion(svnUrl, route, svnUsername, svnPassword);
            Float latestVersion = SVNHelper.getLatestVersion(route, context.getSvnURL(), context.getSvnUsername(), context.getSvnPassword());
            if (latestVersion != null && latestVersion - Math.floor(latestVersion) < 0.05) {
                servicesMajor.add(route);
                route.setVersion(latestVersion);
                System.out.println("Found route " + route.getFullName() + " " + latestVersion);

            } else {
                System.out.println("Ignoring route in non-major version: " + route.getFullName() + ":" + latestVersion);
            }
        }
        return servicesMajor;
    }


    public static void main(String[] args) throws Exception {
        Context context = new Context("/home/dutoitc/git/tatoo-tools/tatoo-tools/builder/context.properties");
//		TalendCommandLineWrapper cmdline = new TalendCommandLineWrapper(context.getCommandlineServerURL(), context.getCommandlineServerPort());
//		cmdline.connect(context.getTacURL(), context.getTacUsername(), context.getTacPassword());
//		cmdline.loginProject(context.getProject(), context.getSvnUsername(), context.getSvnPassword());
//		List<Job> jobs = new SVNVersionsFinder(context).findJobsInMajorVersion(".*", cmdline, null);
//		for (Job job: jobs) {
//			System.out.println(job);
//		}
//		cmdline.stop();


        try (
                TalendCommandLineWrapper cmdline = new TalendCommandLineWrapper(context.getCommandlineServerURL(), context.getCommandlineServerPort());
        ) {
            cmdline.connect(context.getTacURL(), context.getTacUsername(), context.getTacPassword());
            cmdline.loginProject(context.getProject(), context.getSvnUsername(), context.getSvnPassword());
            List<Service> services = new SVNVersionsFinder(context).findServicesInMajorVersion(".*", cmdline, null);
            for (Service service : services) {
                System.out.println(service);
            }
        }

    }


}
