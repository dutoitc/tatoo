package ch.mno.tatoo.facade.karaf;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.RuntimeSshException;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumSet;

/**
 * SSH transport layer on Karaf
 * Created by dutoitc on 16/11/17.
 */
public class KarafSSHTransport implements AutoCloseable {

    private static final int IDLE_TIMEOUT = 30000;
    public static final int CONNECTION_NB_RETRY = 3;
    public static final int CONNECTION_RETRY_DELAY_SEC = 10;
    private ClientSession session;
    public static Logger LOG = LoggerFactory.getLogger(KarafSSHTransport.class);

    KarafSSHTransport(String hostname, int port, String user, String password) {
        try {
            SshClient client = ClientBuilder.builder().build();
            client.getProperties().put("idle-timeout", String.valueOf(IDLE_TIMEOUT));
            client.setKeyPairProvider(new FileKeyPairProvider());
            client.start();

            session = connectWithRetries(client, user, hostname, port, CONNECTION_NB_RETRY, CONNECTION_RETRY_DELAY_SEC);
            session.addPasswordIdentity(password);
            session.auth().verify();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String execute(String command) throws IOException {
        LOG.info("   > " + command);
        ChannelExec channel = session.createExecChannel(command + "\n");
        channel.setIn(new ByteArrayInputStream(new byte[0]));
        channel.setAgentForwarding(true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        channel.setOut(out);
        channel.setErr(err);
        channel.open().verify();
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
        String res = out.toString("UTF-8");
        LOG.info("   < " + res);
        String errStr = err.toString("UTF-8");
        if (errStr != null && !errStr.isEmpty()) {
            LOG.info("   < ERR:" + err);
            throw new RuntimeException(errStr);
        }
        return res;
    }


    private static ClientSession connectWithRetries(SshClient client, String user, String host, int port, int nbRetry, int retryDelaySec) throws Exception, InterruptedException {
        ClientSession session = null;
        int retries = 0;

        do {
            ConnectFuture future = client.connect(user, host, port);
            future.await();

            try {
                session = future.getSession();
            } catch (RuntimeSshException var6) {
                if (retries++ >= nbRetry) {
                    throw var6;
                }

                Thread.sleep((long) (retryDelaySec * 1000));
                LOG.trace("retrying (attempt " + retries + ") ...");
            }
        } while (session == null);

        return session;
    }


    @Override
    public void close() throws Exception {
        try {
            session.close();
        } catch (Exception e) {
            // Do nothing
        }
    }

}
