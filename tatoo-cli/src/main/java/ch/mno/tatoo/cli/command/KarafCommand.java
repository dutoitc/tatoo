package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.facade.bonita.BonitaDeployReport;
import ch.mno.tatoo.facade.bonita.BonitaFacade;
import ch.mno.tatoo.facade.bonita.BonitaProcess;
import ch.mno.tatoo.facade.bonita.BonitaUndeployReport;
import ch.mno.tatoo.facade.common.FacadeException;
import ch.mno.tatoo.facade.karaf.KarafSSHFacade;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Bonita API commands
 * Created by dutoitc on 23/11/17.
 */
public class KarafCommand extends AbstractCommand {


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("karaf:install [bundleName] [bundle.jar]", "Installe un jar (clean, install, diag) (suppose jar sur /tmp/bundle.jar)")
        );
    }

    @Override
    public void handle(List<String> args) {
        if (args.isEmpty()) return;
        switch (args.get(0)) {
            case "karaf:install":
                karafInstall(args.get(1), args.get(2));
                break;
        }
    }

    private void karafInstall(String bundleName, String bundleJar)  {
        System.out.println("karaf:install\n   bundleName="+bundleName+"\n   bundleJar="+bundleJar);
        try {
            KarafSSHFacade facade = (KarafSSHFacade) buildKarafFacade();
            facade.clean(false, bundleName);
            int bundleId = facade.install("file:/tmp/"+bundleJar, true);
            System.out.println("Installed " + bundleJar + ", bundleId="+bundleId);
            String diag = facade.diag(bundleName);
            if (StringUtils.isEmpty(diag)) {
                System.out.println("Diag: ok");
            } else {
                System.err.println("Diag: ko\n" + diag);
            }
        } catch (Exception e) {
            System.err.println("Cannot execute karafInstall: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ex: karaf:install "Homere2 :: OSGI" homere2-2.0.0-SNAPSHOT.jar

}
