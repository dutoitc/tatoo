package ch.mno.tatoo.cli;

import ch.mno.tatoo.cli.command.AbstractCommand;
import ch.mno.tatoo.cli.command.CommandsHelper;
import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.common.reporters.ConsoleReporter;
import ch.mno.tatoo.common.reporters.JSonStatusReporter;
import ch.mno.tatoo.common.reporters.OnlyStatusReporter;
import ch.mno.tatoo.common.reporters.Reporter;

import java.io.IOException;
import java.util.ArrayList;
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
        for (AbstractCommand command: commands) {
            if (command.canHandle(argsMod)) {
                reporter.showWelcomeMessage();
                long t0 = System.currentTimeMillis();
                command.handle(argsMod);
                reporter.logTrace("Command " + command.getClass().getSimpleName()+" terminée en " + (System.currentTimeMillis()-t0)/1000 + "s.");
                reporter.showGoodbyeMessage();
                System.exit(0);
            }
        }
        System.err.println("Commande non reconnue: " + argsMod);
    }




}
