package ch.mno.tatoo.facade.karaf.commands;

import ch.mno.tatoo.facade.karaf.KarafSSHTransport;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Clean Karaf for root application
 * Created by dutoitc on 01/12/17.
 */
public class KarafCleanAction extends AbstractKarafAction {

    private final String groupIdBase;
    private Pattern regex;


    public KarafCleanAction(String groupIdBase, String regexStr) {
        if (StringUtils.isEmpty(regexStr)) {
            throw new RuntimeException("Regex ne peut être vide");
        }
        regex = Pattern.compile(regexStr);
        this.groupIdBase=groupIdBase;
    }


    private String executeCommand(String command, Supplier<KarafSSHTransport> transportBuilder) throws Exception {
        try (KarafSSHTransport transport = transportBuilder.get()) {
            String res = transport.execute(command);
            return res;
        }
    }


    @Override
    public void execute(final Supplier<KarafSSHTransport> transportBuilder) throws Exception {

        // Désinstaller les bundles
        Arrays.asList(executeCommand("bundle:list -l | grep " + groupIdBase, transportBuilder).split("\n")).stream()
                .filter(f-> regex.matcher(f).find())
                .map(f -> f.split("│"))
                .filter(f -> f.length == 5)
                .peek(f -> reporter.logTrace("Uninstalling " + f[4]))
                .map(f -> f[0])
                .forEach(f -> {
                    try {
                        String command = "bundle:uninstall " + f;
                        if (isDryMode()) {
                            reporter.logInfo("Drymode: " + command);
                        } else {
                            // TODO: improvment: bundle:uninstall id1 id2 id3
                            executeCommand(command, transportBuilder);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        // Désinstaller les features
        Arrays.asList(executeCommand("feature:list -r | grep " + groupIdBase, transportBuilder).split("\n")).stream()
                .map(f -> f.split("│"))
                .filter(f -> f.length == 5)
                .peek(f -> reporter.logTrace("Uninstalling " + f[4]))
                .map(f -> f[0])
                .forEach(f -> {
                    try {
                        executeCommand("feature:uninstall " + f, transportBuilder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


        // Désinstaller les repo-list
        Arrays.asList(executeCommand("feature:repo-list | grep " + groupIdBase, transportBuilder).split("\n")).stream()
                .filter(f-> regex.matcher(f).find())
                .map(f -> f.split("│"))
                .filter(f -> f.length == 2)
                .peek(f -> reporter.logTrace("Uninstalling " + f[1]))
                .map(f -> f[0])
                .forEach(f -> {
                    try {
                        String command = "feature:uninstall " + f;
                        if (isDryMode()) {
                            reporter.logInfo("Drymode: " + command);
                        } else {
                            executeCommand(command, transportBuilder);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        String command = "feature:repo-remove " + f;
                        if (isDryMode()) {
                            reporter.logInfo("Drymode: " + command);
                        } else {
                            executeCommand(command, transportBuilder);
                        }
                        // Si des features les utilise, message d'erreur de type "Error executing command: The following features are required from the repository: INFRA_Service_Ping-feature/18.11.0"
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


        // TODO: utile ?
//        List<KarafElement> list = karafFacade.findKarafElement().stream()
//                .filter(KarafElement::isTatoo)
//                .collect(Collectors.toList());
//        if(context.isDryRun()) {
//            LOG.info("   dry-run: Karaf clean would have cleaned " + list.size());
//        } else {
//            LOG.info("   Karaf clean of " + list.size() + " bundles");
//            String ids = list.stream()
//                    .peek(e -> report.addKarafClean(e.getLocation()))
//                    .map(e -> e.getId())
//                    .collect(Collectors.joining(" "));
//            karafFacade.execute(new GenericCommand("uninstall " + ids));
//        }

    }


}
