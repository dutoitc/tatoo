package ch.mno.tatoo.facade.bonita;

import ch.mno.tatoo.facade.common.FacadeException;

/**
 * Created by dutoitc on 30/04/18.
 */
public class BonitaCleanerMain {


    public static void main(String[] args) throws FacadeException {
        String username = "administrator";
        String password = "administrator";
        String serverURL = "http://myserver:8280";
        String applicationName = "bonita";

        BonitaFacade facade = new BonitaFacade(serverURL, applicationName, username, password);
        System.out.println("A cleaner:");
        facade.findProcesses(null).forEach(p -> {
            try {
                long nbCases = facade.countProcessInstances(p.getProcessId());
                if (nbCases > 0) {
                    System.out.println("Process instances of processDefinitionId=" + p.getId() + " - " + p.getName() + ":" + p.getVersion() + ", nbCases=" + nbCases);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });

        System.out.println("\n\nCleanup...");
        facade.findProcesses(null).forEach(p -> {
            try {
                long nbCases = facade.countProcessInstances(p.getProcessId());
                if (nbCases > 0) {
                    System.out.println("Deleting process instances of processDefinitionId=" + p.getId() + " - " + p.getName() + ":" + p.getVersion() + ", nbCases=" + nbCases);
                    facade.deleteProcessInstances(p.getProcessId(), 10000);
                }
            } catch (FacadeException e) {
                e.printStackTrace();
            }
        });

    }


}
