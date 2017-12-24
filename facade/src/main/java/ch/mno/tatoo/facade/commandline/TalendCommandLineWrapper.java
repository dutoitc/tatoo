package ch.mno.tatoo.facade.commandline;

import ch.mno.tatoo.facade.commandline.commands.AbstractCommand;
import ch.mno.tatoo.facade.commandline.data.CommandFailedException;
import ch.mno.tatoo.facade.commandline.data.Job;
import ch.mno.tatoo.facade.commandline.telnet.TelnetWrapper;
import ch.mno.tatoo.facade.commandline.telnet.ExecutionFailedException;
import ch.mno.tatoo.facade.commandline.telnet.TimeoutException;
import ch.mno.tatoo.facade.commandline.telnet.WrongCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper to easy call command on a Talend Telnet server.
 * Each command will wait until completion or timeout, even if the server is asynchronous.
 * Created by dutoitc on 11/03/15.
 */
public class TalendCommandLineWrapper implements AutoCloseable {

    public static final int TIMEOUT_CONNECT_SEC = 60;
    public static final int TIMEOUT_LOGIN_SEC = 60 * 10; // SVN Update could take time -> 3' could be small time
    public static final int TIMEOUT_RUNJOB_SEC = 60 * 15; // Compilation could take some time
    private static Logger LOG = LoggerFactory.getLogger(TalendCommandLineWrapper.class);
    private final TelnetWrapper wrapper;


    public TalendCommandLineWrapper(String server, int port) {
        wrapper = new TelnetWrapper(server, port);
    }

    public TalendCommandLineWrapper(TelnetWrapper wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Connect to a TAC
     */
    public void connect(String tacServer, String tacUsername, String tacPassword) throws ExecutionFailedException {
        LOG.info("Connecting to server " + tacServer + " with username " + tacUsername);
        wrapper.waitFor("talend>");

        // >>> [initRemote...]
        wrapper.send("initRemote " + tacServer + " -ul " + tacUsername + " -up " + tacPassword);
        waitForLastCommandCompletion(TIMEOUT_CONNECT_SEC);
        LOG.info("Connected.");
    }

    /**
     * Login on a project
     */
    public void loginProject(String projectName, String username, String password) throws ExecutionFailedException {
        LOG.info("Login to project with /" + projectName + "/" + username + "/");
        long t0 = System.currentTimeMillis();
        // >>> [logonProject...]
        wrapper.send("logonProject -pn " + projectName + " -ul \"" + username + "\" -up \"" + password + "\"");
        waitForLastCommandCompletion(TIMEOUT_LOGIN_SEC);
        LOG.info("Logged in after " + (System.currentTimeMillis() - t0) / 1000 + "s.");
    }


    /**
     * Run a job on the server (executeJobOnServer)
     *
     * @return job command line
     */
    public String runJobOnServer(Job job, String server) throws ExecutionFailedException {
        LOG.info("Running job on server" + job.getName());
        wrapper.send("executeJobOnServer " + job.getName() + " -es " + server);
        String commandId = waitForLastCommandCompletion(TIMEOUT_RUNJOB_SEC);

        // Loop until timeout or getCommandStatus startsWith("COMPLETED") ou FAILED ?
        long timeout = System.currentTimeMillis() + TIMEOUT_RUNJOB_SEC;
        if (waitCommandCompletion(commandId, timeout)) return wrapper.getLastData();


        return wrapper.getLastData();
    }


    /**
     * Run a job on the server (executeJobOnServer)
     *
     * @return job command line
     */
    public String executeJob(Job job) throws ExecutionFailedException {
        LOG.info("Running job" + job.getName());
        wrapper.send("executeJob " + job.getName() + " -jt " + TIMEOUT_RUNJOB_SEC + " -i /usr/bin/java");
        String commandId = waitForLastCommandCompletion(TIMEOUT_RUNJOB_SEC + 5);

        // Loop until timeout or getCommandStatus startsWith("COMPLETED") ou FAILED ?
        long timeout = System.currentTimeMillis() + TIMEOUT_RUNJOB_SEC + 5;
        if (waitCommandCompletion(commandId, timeout)) return wrapper.getLastData();

        // commit, test


        return wrapper.getLastData();
    }

    private boolean waitCommandCompletion(String commandId, long timeout) {
        boolean first = true;
        while (first || System.currentTimeMillis() < timeout) {
            first = false;
            wrapper.send("getCommandStatus " + commandId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String lastData = wrapper.getLastData();
            if (lastData.startsWith("COMPLETED") || lastData.startsWith("FAILED") || lastData.startsWith("java.lang")) { // java.lang=exception
                return true;
            }
        }
        return false;
    }


    public void execute(AbstractCommand command) throws ExecutionFailedException, CommandFailedException {
        String commandStr = command.build();
        LOG.info("Running " + command.getClass().getSimpleName() + ": \t" + commandStr);
        wrapper.send(commandStr);

        long t0 = System.currentTimeMillis();
        if (command.isImmediate()) {
            waitFor("talend>", TIMEOUT_RUNJOB_SEC);
            command.setLastData(wrapper.getLastData());
        } else {

            String commandId = waitForLastCommandCompletion(TIMEOUT_RUNJOB_SEC);

            wrapper.send("getCommandStatus " + commandId);
            try {
                Thread.sleep(1000); // Immediate result, but give some time to the server
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String log = wrapper.getLastData();
            if (!log.startsWith("COMPLETED")) {
                throw new CommandFailedException(command, log);
            }
            command.setLastData(log);
        }
        long dt = System.currentTimeMillis() - t0;
        LOG.info("Execution done. " + command.toString() + " ran in " + dt / 1000 + "s.");
    }


    private static void waitForCommand(TelnetWrapper wrapper, String commandId, int maxSeconds) throws ExecutionFailedException {
        LOG.info("Wait for command " + commandId);
        long maxTime = System.currentTimeMillis() + maxSeconds * 1000;
        while (System.currentTimeMillis() < maxTime) {
//			wrapper.send("listCommand -a");
            wrapper.send("getCommandStatus " + commandId);
            try {
                String line = wrapper.waitFor("COMPLETED", "FAILED", "ERROR");
//				System.out.println("line=["+line+"]");
                if (line.startsWith("COMPLETED")) {
                    return;
                } else if (line.startsWith("FAILED")) {
                    throw new ExecutionFailedException("Command failed: " + line, wrapper.getLastData());
                } else if (line.startsWith("ERROR")) {
                    throw new RuntimeException("Command error: " + line);
                }
            } catch (TimeoutException e) {
                // Just retry
            }
            try {
                Thread.sleep(1000); // listCommand could be time-consuming. Avoid charge on server !
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (wrapper.getLastData().contains("Talend Commandline Plugin"))
            throw new WrongCommandException(wrapper.getLastData());
        throw new TimeoutException("Command not found after some time. LastData:" + wrapper.getLastData());
    }

    /**
     * @return ID of the command (could be chained to getCommandStatus for detail)
     */
    private String waitForLastCommandCompletion(int maxSeconds) throws ExecutionFailedException {
        // <<< ADDED_COMMAND n
        String line = wrapper.waitFor("ADDED_COMMAND");
        String commandId = line.split(" ")[1];

        // >>> [listCommand -a] and [n:COMPLETED] ou [n:RUNNING...] ou [n:ERROR]
        waitForCommand(wrapper, commandId, maxSeconds);
        return commandId;
    }

    /**
     * @return ID of the command (could be chained to getCommandStatus for detail)
     */
    private void waitFor(String keyword, int maxSeconds) throws ExecutionFailedException {
        // <<< ADDED_COMMAND n
        String line = wrapper.waitFor(maxSeconds * 1000, keyword);
    }

    public String getLastData() {
        return wrapper.getLastData();
    }

    public String getAllData() {
        return wrapper.getAllData();
    }

    @Override
    public void close() throws Exception {
        LOG.info("Stopping");
        // Clean exit
        wrapper.send("quit");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        wrapper.stop();
        Thread.sleep(300); // Wait for commandline stop and log4j flush
        LOG.info("Stopped");
    }

}
