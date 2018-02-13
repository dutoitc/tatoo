package ch.mno.tatoo.cli.command;

import java.util.Arrays;
import java.util.List;

/**
 * Created by xsicdt on 23/11/17.
 */
public class ProjectCommand extends AbstractCommand {

    public void postInit() {
        super.postInit();
    }

    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("project:create_tag [project] [version]", "Create a SVN TAG for the project")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size() > 0 && args.get(0).startsWith("project:")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        if (args.size() < 1) return;
        if (args.size() < 3) throw new RuntimeException("Not enouth arguments");

        try {
            switch (args.get(0)) {
                case "project:create_tag":
                    buildTACFacade().createTag(args.get(1), args.get(2), reporter);
                    break;
                default:
                    reporter.logError("Unrecognized command: " + args.get(0));
            }
        } catch (Exception e) {
            reporter.logError("Erreur au " + args.get(0) + e.getMessage());
        }
    }


}