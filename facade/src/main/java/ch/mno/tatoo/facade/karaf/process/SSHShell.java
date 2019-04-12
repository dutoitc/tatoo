package ch.mno.tatoo.facade.karaf.process;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

/**
 * Created by dutoitc on 17/08/15.
 */
public class SSHShell implements DataListener {

	private static Logger LOG = LoggerFactory.getLogger(SSHShell.class);
	public static final int TIME_TO_WAIT_BEFORE_SENDING_DATA_MSEC = 100; // no data from server after this time will flush data to dataListener
	private final int TIMEOUT = 10000;

	private String hostname;
	private int port;
	private String user;
	private String password;
	private Connection connection;
	private Session session;
	private ServerListener serverListener;

	private StringBuffer buffer = new StringBuffer();


	public SSHShell(String hostname, int port, String user, String password) throws IOException {
		this.hostname = hostname;
		this.port = port;
		this.user = user;
		this.password = password;
		connect();
	}


	private void connect() throws IOException {
		LOG.info("SSH connection to " + hostname + ":" + port + "...");
		connection = new Connection(hostname, port);
		connection.connect();
		LOG.info("   connected.");

		boolean isAuthenticated = connection.authenticateWithPassword(user, password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		LOG.info("   authenticated.");

		session = connection.openSession();
		session.startShell();
		LOG.info("   session opened");
		serverListener = new ServerListener(session.getStdout(), this);
		new Thread(serverListener).start();
		//session.execCommand("help");//"features:listurl\r\n");
	}

	public void stop() {
		try {
			serverListener.stop();
		}
		catch (Exception e) {
		}
		try {
			if (session != null) session.close();
		}
		catch (Exception e) {
		}
		try {
			if (connection != null) connection.close();
		}
		catch (Exception e) {
		}
	}

	public String execute(String line, int maxResponseTimeMSec, int maxInactivityTimeMSec) throws IOException {
		buffer = new StringBuffer();
		session.getStdin().write((line + "\r\n").getBytes());
		session.getStdin().flush();
		LOG.info("   sent command > " + line);

		long maxTime = System.currentTimeMillis() + maxResponseTimeMSec;
		int lastSize = buffer.length();
		//boolean firstLine=true;
		while (System.currentTimeMillis() < maxTime) {
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			int size = buffer.length();
			if (size > lastSize) {
				//if (firstLine) {
				//	firstLine = false; // Some command send first line immediately, before computing result
				//	maxTime = System.currentTimeMillis() + maxResponseTimeMSec;
				//} else {
					maxTime = System.currentTimeMillis() + maxInactivityTimeMSec;
				//}

				lastSize = size;
//				System.out.println("Buffer size is " + size);
			}
		}
		return buffer.toString();
	}


	@Override
	public void newLine(String line) {
		buffer.append(line);
	}


	/**
	 * Listen to commandlineServer. When data is available, send it to the listener.
	 */
	private static class ServerListener implements Runnable {

		private final DataListener dataListener;
		private boolean shouldStop = false;
		private StringBuilder buffer = new StringBuilder();
		private InputStream in;
		private long lastReceiveTime = 0; // Le serveur n'envoie pas de caractère de fin de ligne. On doit donc détecter une fin de transmission... par le temps.

		public ServerListener(InputStream in, DataListener dataListener) {
			this.in = in;
			this.dataListener = dataListener;
		}

		@Override
		public void run() {
			while (!shouldStop) {
				try {
					while (in.available() > 0) { // FIXME: not optimized. Read into bytes buffer.
						int i = in.read();
						char c = (char) i;
						buffer.append(c);
						lastReceiveTime = System.currentTimeMillis();
					}

					// No data for some time, and something to send -> send to listener, and clean cache
					if (lastReceiveTime != 0 && System.currentTimeMillis() > lastReceiveTime + TIME_TO_WAIT_BEFORE_SENDING_DATA_MSEC) {
						dataListener.newLine(buffer.toString());
						buffer = new StringBuilder();
						lastReceiveTime = 0;
					}
					try {
						Thread.sleep(10); // Avoid DoS on server ;-)
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		/**
		 * Clean stop
		 */
		public void stop() {
			shouldStop = true;
		}
	}


}
