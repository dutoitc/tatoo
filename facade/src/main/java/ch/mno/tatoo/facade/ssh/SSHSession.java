package ch.mno.tatoo.facade.ssh;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dutoitc on 30/09/16.
 */
public class SSHSession implements AutoCloseable {

    private Session session;

    public SSHSession(String host, String username, String password) throws JSchException, IOException {
        JSch jsch = new JSch();
        String defaultSSHDir = System.getProperty("user.home") + "/.ssh";
        String knownHosts = defaultSSHDir + "/known_hosts";
        jsch.setKnownHosts(knownHosts);
        session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        // FIXME: know_hosts file seems not to be read ?!?
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);


        try {
            session.connect();
        } catch (JSchException e){
            if (e.getMessage().contains("UnknownHostKey")) {
                throw new IOException("Erreur, vérifiez les clés dans "+knownHosts+": " + e.getMessage());
            } else {
                throw e;
            }
        }
    }

    public String execute(String command) throws JSchException, IOException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        ((ChannelExec) channel).setPty(true);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();

        byte[] tmp = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                sb.append(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }

        channel.disconnect();
        return sb.toString();
    }


    @Override
    public void close() throws Exception {
        session.disconnect();
    }
}
