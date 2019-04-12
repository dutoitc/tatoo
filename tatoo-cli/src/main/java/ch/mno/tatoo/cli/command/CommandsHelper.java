package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.common.reporters.Reporter;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by dutoitc on 23/11/17.
 */
public class CommandsHelper {

    /**
     * Find instances of AbstractCommand in this folder, then inject properties and reporter
     * @param properties
     * @param reporter
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<AbstractCommand> findCommands(RuntimeProperties properties, Reporter reporter) throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(CommandsHelper.class.getPackage().getName());
        Set<Class<? extends AbstractCommand>> commands =reflections.getSubTypesOf(AbstractCommand.class);
        List<AbstractCommand> ret = new ArrayList<>(commands.size());
        for (Class<? extends AbstractCommand> command : commands) {
            AbstractCommand e = command.newInstance();
            e.setProperties(properties);
            e.setReporter(reporter);
            e.postInit();
            ret.add(e);
        }
        return ret;
    }




    public static String getUsage() throws InstantiationException, IllegalAccessException {
        List<AbstractCommand> commands = findCommands(null, null);
        StringBuilder sb = new StringBuilder();
        sb.append("\n________________________.o[ Tatoo-CLI ]o.____________________________________________________________\n\n");
        sb.append("Usage: tatoo-cli [options] [commandes]\n");
        sb.append("  options: \n");
        sb.append("    --help                       : Affiche cette aide\n");
        sb.append("    --properties=filename        : Fichier de propriétés. Défaut: tatoo-cli.properties\n");
        sb.append("    --json                       : Sortie JSON (défaut: console)\n");
        sb.append("    --onlyStatus                 : Sortie status uniquement (OK, KO messages...) (défaut: console)\n");
        sb.append("    --drymode                    : Aucune action destructrice (deploy/undeploy, install) = mode simulation (excepté list/get/read)\n");
        sb.append("    --linuxUsername=xxx          : Nom d'utilisateur linux utilisé pour quelques commands (service status, stop/start) (défaut: $USER)\n");
        sb.append("    --linuxPassword=xxx          : Mot de passe linux\n");
        sb.append("  commandes: (le regex fonctionne en mode 'find'. Pour une recherche 'match', utiliser \"^something$\"\n");

        commands.stream()
                .sorted(Comparator.comparing(a -> a.getClass().getSimpleName()))
                .flatMap(c->c.getUsage().stream())
                .forEach(c-> sb.append("    ").append(StringUtils.rightPad(c.getCommand(), 30)).append(": ").append(c.getLibelle()).append('\n'));
        sb.append('\n');
        return sb.toString();
    }

}
