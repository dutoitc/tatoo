package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.facade.tac.TACFacade;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dutoitc on 23/11/17.
 */
public class CreateCronTriggerCommand extends AbstractCommand {


    public void postInit() {
        super.postInit();
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("createCronTrigger [task] [label] [description|hours|minutes|daysOfMonth|daysOfWeek|months|years]*", "Créer un trigger cron sur un job donné par son label")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("createCronTrigger")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() > 0 && args.get(0).equals("createCronTrigger")) {
            if (args.size() < 3) throw new RuntimeException("Le task et le label sont obligatoires");
            try {
                createCronTrigger(args.get(1), args.get(2), args.subList(3, args.size()));
            } catch (Exception e) {
                reporter.logError("Erreur au deploy: " + e.getMessage());
            }
        }
    }

    private void createCronTrigger(String task, String label, List<String> args) throws Exception {
        TACFacade tacFacade = buildTACFacade();
        long taskId = tacFacade.getTaskIdByName(task);

        String description="-";
        String hours="*";
        String minutes="*";
        String daysOfMonth="*";
        String daysOfWeek="*";
        String months="*";
        String years="*";

        for (String arg: args) {
            String[] spl = arg.split("=");
            if (spl.length!=2) throw new RuntimeException("Paramètre invalide: " + arg);

            switch(spl[0]) {
                case "description": description = spl[1];break;
                case "hours":hours = spl[1];break;
                case "minutes":minutes = spl[1];break;
                case "daysOfMonth":daysOfMonth = spl[1];break;
                case "daysOfWeek":daysOfWeek = spl[1];break;
                case "months":months = spl[1];break;
                case "years":years = spl[1];break;
                default: throw new RuntimeException("Paramètre inconnu: " + arg);
            }
        }

        tacFacade.createCronTrigger(""+taskId, label, description, hours, minutes, daysOfMonth, daysOfWeek, months, years);
    }


}