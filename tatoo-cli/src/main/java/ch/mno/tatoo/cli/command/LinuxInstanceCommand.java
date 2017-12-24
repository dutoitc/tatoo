package ch.mno.tatoo.cli.command;

import ch.mno.tatoo.common.properties.RuntimeProperties;
import ch.mno.tatoo.facade.ssh.SSHSession;
import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dutoitc on 23/11/17.
 * STATUS: POC (not yet working)
 */
public class LinuxInstanceCommand extends AbstractCommand {


    @Override
    public List<UsageItem> getUsage() {
        return Arrays.asList(
                new UsageItem("cmdline:status", "Affiche le status de l'instance talend-cmdline"),
        new UsageItem("rjs:status", "Affiche le status de l'instance talend-rjs (remote job server)"),
        new UsageItem("tac:status", "Affiche le status de l'instance talend-tac (Talend Administration Center)"),
        new UsageItem("nexus:status", "Affiche le status de l'instance Nexus")
//            new UsageItem("cmdline:start", "Démarre l'instance talend-cmdline"),
//            new UsageItem("cmdline:stop", "Stoppe l'instance talend-cmdline")
        );
    }

    @Override
    public boolean canHandle(List<String> args) {
        if (args.size()==1 && args.get(0).equals("cmdline:status")) return true;
        if (args.size()==1 && args.get(0).equals("rjs:status")) return true;
        if (args.size()==1 && args.get(0).equals("tac:status")) return true;
        if (args.size()==1 && args.get(0).equals("nexus:status")) return true;
//        if (args.size()==1 && args.get(0).equals("cmdline:start")) return true;
//        if (args.size()==1 && args.get(0).equals("cmdline:stop")) return true;
        return false;
    }

    @Override
    public void handle(List<String> args) {
        String serverDS = properties.get(RuntimeProperties.PROPERTIES.SERVER_DS);
        String linuxUsername = properties.get("linuxUsername");
        String linuxPassword = properties.get("linuxPassword");
        if (args.size()==1 && args.get(0).equals("cmdline:status")) {
            executeSsh(serverDS, linuxUsername, linuxPassword, "service talend-cmdline status", "Checking status of talend commandliner server");
        } else if (args.size()==1 && args.get(0).equals("rjs:status")) {
            findStatus(serverDS, linuxUsername, linuxPassword, "talend-rjs-6.4.1.service");
        } else if (args.size()==1 && args.get(0).equals("tac:status")) {
            findStatus(serverDS, linuxUsername, linuxPassword, "talend-tac-6.4.1.service");
        } else if (args.size()==1 && args.get(0).equals("nexus:status")) {
            findStatus(serverDS, linuxUsername, linuxPassword, "talend-nexus-6.4.1.service");


            // stop-start: Interactive authentication required
//        } else if (args.size()==1 && args.get(0).equals("cmdline:start")) {
//            executeSsh(serverDS, linuxUsername, linuxPassword, "service talend-cmdline start", "Checking status of talend commandliner server");
//        } else if (args.size()==1 && args.get(0).equals("cmdline:stop")) {
//            executeSsh(serverDS, linuxUsername, linuxPassword, "service talend-cmdline stop", "Checking status of talend commandliner server");
        }
    }


    private void findStatus(String serverDS, String linuxUsername, String linuxPassword, String service) {
        try {
            if (linuxPassword==null) throw new RuntimeException("Please add --linuxPassword=...");
            reporter.logInfo("ssh "+linuxUsername+"@"+serverDS);
            SSHSession session = new SSHSession(serverDS, linuxUsername, linuxPassword);
            String ret = session.execute("systemctl status " + service);
            Pattern pat = Pattern.compile("Active: (.*) since");
            Matcher matcher = pat.matcher(ret);
            if (matcher.find()) {
                reporter.logInfo("Service " + service + ": " + matcher.group(1));
            } else {
                System.out.println("Service " + service + ": unknown\n" +ret);
            }
            session.close();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        reporter.logError("Service " + service + ": unknown");
    }


    private void executeSsh(String serverDS, String linuxUsername, String linuxPassword, String command, String id) {
        try {
            if (linuxPassword==null) throw new RuntimeException("Please add --linuxPassword=...");
            reporter.logInfo("ssh "+linuxUsername+"@"+serverDS);
            SSHSession session = new SSHSession(serverDS, linuxUsername, linuxPassword);
            String ret = session.execute("echo \"" + linuxPassword + "\" | sudo -S -u username " + command);
            if (ret.contains("active (running)")) {
                reporter.logInfo("Service talend-cmdline-6.4.1 status: running");
            } else if (ret.contains(id)) {
                String status = ret.substring(ret.indexOf(id)+id.length());
                reporter.logInfo("Service talend-cmdline-6.4.1 status: " + status);
            } else {
                reporter.logInfo("Service talend-cmdline-6.4.1 status: " + ret);
            }
            session.close();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
