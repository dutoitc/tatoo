package ch.mno.tatoo.cli;

import ch.mno.tatoo.cli.command.AbstractCommand;
import ch.mno.tatoo.cli.command.CommandsHelper;
import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.common.reporters.ConsoleReporter;
import ch.mno.tatoo.common.reporters.JSonStatusReporter;
import ch.mno.tatoo.common.reporters.OnlyStatusReporter;
import ch.mno.tatoo.common.reporters.Reporter;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public class TatooCliMain {

    // TODO:
    // - tatoo-cli undeploy (undeploy via tac)   (NOTE: utilisation de cleanup actuellement)
    // - tatoo-cli status (vérifications globales Tatoo, doit être OK excepté les services et routes, pas encore déployées) + volumétries DB
    // - tatoo-cli postdeploy (exécuter POSTDEPLOY_MajStgSequence, POSTDEPLOY_BpmIndexes, POSTDEPLOY_MdmIndexes)




    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException {
        String propertiesFilename = "tatoo-cli.properties";
        Reporter reporter = new ConsoleReporter();
        String linuxUsername = System.getenv("USER");
        String linuxPassword = null; // Used for some linux-domain commands
        boolean dryRun = false;

        // Keep and check args
        List<String> argsMod = new ArrayList<>(args.length);
        for (int i=0; i<args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--help")) {
                reporter.logInfo(CommandsHelper.getUsage());
                System.exit(0);
            } else if (arg.startsWith("--properties=")) {
                propertiesFilename=arg.substring(13);
            } else  if (arg.startsWith("--json")) {
                reporter = new JSonStatusReporter();
            } else if (arg.startsWith("--onlystatus")) {
                reporter = new OnlyStatusReporter();
            } else  if (arg.startsWith("--dryrun")) {
                dryRun=true;
            } else if (arg.startsWith("--linuxUsername=")) {
                linuxUsername = arg.substring(16);
            } else if (arg.startsWith("--linuxPassword=")) {
                linuxPassword = arg.substring(16);
            } else{
                argsMod.add(arg);
            }
        }

        // Build support classes
        RuntimeProperties properties = new RuntimeProperties(propertiesFilename, reporter);
        properties.setDryRun(dryRun);
        List<AbstractCommand> commands = CommandsHelper.findCommands(properties, reporter);
        if (linuxUsername!=null) {
            properties.put("linuxUsername", linuxUsername);
        }
        if (linuxPassword!=null) {
            properties.put("linuxPassword", linuxPassword);
        }


        // Run
        reporter.showWelcomeMessage();
        if (argsMod.size()==1 && argsMod.get(0).endsWith(".cli")) {
            runManyCommands(argsMod.get(0), reporter, commands);
        } else {
            runOneCommand(reporter, argsMod, commands);
        }
    }

    /** Run one command */
    private static void runOneCommand(Reporter reporter, List<String> argsMod, List<AbstractCommand> commands) {
        try {
            if (executeCommand(argsMod, commands, reporter)) {
                reporter.showGoodbyeMessage();
                System.exit(0);
            }
            System.err.println("Invalid command: " + argsMod);
        } catch (RuntimeException e) {
            System.err.println("Error on run: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** Run many commands, stop on error */
    private static void runManyCommands(String filename, Reporter reporter, List<AbstractCommand> commands) throws IOException {
        for (String line: Files.readAllLines(Paths.get(URI.create(filename)))) {
            try {
                // TODO: support spaces in '"'
                if (!executeCommand(Arrays.asList(line.split(" ")), commands, reporter)) {
                    System.err.println("Invalid command: " + line);
                    System.exit(0);
                }
            } catch (RuntimeException e) {
                System.err.println("Error while running " + line + ": " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
        reporter.showGoodbyeMessage();
        System.exit(0);
    }

    /**
     *
     * @param args command-line arguments
     * @param commands every runnable commands
     * @param reporter where to report informations
     * @return true if command has been found and executed
     */
    private static boolean executeCommand(List<String> args, List<AbstractCommand> commands, Reporter reporter) {
        for (AbstractCommand command : commands) {
            if (command.canHandle(args)) {
                long t0 = System.currentTimeMillis();
                command.handle(args);
                reporter.logTrace("Command " + command.getClass().getSimpleName() + " finished in " + (System.currentTimeMillis() - t0) / 1000 + "s.");
                return true;
            }
        }
        return false;
    }



}
