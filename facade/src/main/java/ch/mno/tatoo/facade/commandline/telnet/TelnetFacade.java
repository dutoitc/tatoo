package ch.mno.tatoo.facade.commandline.telnet;


import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dutoitc on 10/03/15.
 * Low-level on a Talend commandline server. Need a commandline server to be running (Talend pro).
 *
 * <p/>
 * Sample usage:
 * <code><pre>
 * LinesListener linesListener = new LinesListener() { @Override public void newLine(String line) { System.out.println("Got line: " + line); } };
 * TelnetWrapper wrapper = new TelnetWrapper("myserver", 8002, linesListener); wrapper.start(); wrapper.serve(); wrapper.stop(); </pre></code>
 *
 * Sample 2:
 * <pre>
      DataListener dataListener = new DataListener() {

			@Override
			public void newLine(String line) {
				System.out.println("Got line: " + line);
			}
		};

 	TelnetFacade wrapper = new TelnetFacade("xxx.yyy.zz", 8002, dataListener);
 	wrapper.start();
 	wrapper.serve();
 	wrapper.stop();
  </pre>
 */
public class TelnetFacade {

	public static final int TIME_TO_WAIT_BEFORE_SENDING_DATA_MSEC = 100; // no data from server after this time will flush data to dataListener
	private Logger LOG = LoggerFactory.getLogger(TelnetFacade.class);
	private final int commandlineServerPort;
	private final String commandlineServer;
	private TelnetClient tc;
	private OutputStream out;
	private InputStream in;
	private ServerListener serverListener;
	private DataListener dataListener;

	/**
	 * Instance initialization
	 */
	public TelnetFacade(String commandlineServer, int commandlineServerPort, DataListener dataListener) {
		this.commandlineServer = commandlineServer;
		this.commandlineServerPort = commandlineServerPort;
		this.dataListener = dataListener;
	}

	/**
	 * Start Telnet Wrapper (connection to server, open streams).
	 */
	public void start() {
		LOG.info("Starting telnet wrapper");
		tc = new TelnetClient();
		try {
			LOG.info("Connecting to remote Telnet Server...");
			tc.connect(commandlineServer, commandlineServerPort);
			LOG.info("   done.");
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Impossible de se connecter au serveur Telnet " + commandlineServer + ":" + commandlineServerPort);
		}

		in = tc.getInputStream();
		out = tc.getOutputStream();
		LOG.info("Started telnet wrapper");
	}


	/**
	 * Stop Telnet Wrapper (disconnection, close streams)
	 */
	public void stop() {
		LOG.info("Disconnecting from commandlineServer...");
		try {
			if (serverListener != null) {
				serverListener.stop();
			}
			tc.disconnect();
			LOG.info("   done.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Serve. A thread will send read data to dataListener.
	 */
	public void serve() {
		LOG.info("Starting to serve");
		serverListener = new ServerListener(tc.getInputStream(), dataListener);
		new Thread(serverListener).start();
	}

	public void send(String line) {
		LOG.info(">>>" + line);
		// Send newline after each command
		line+=(char)0x0d;
		line+=(char)0x0a;

		try {
			out.write(line.getBytes());
			out.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listen to commandlineServer. When data is available, send it to the listener.
	 */
	private static class ServerListener implements Runnable {

		private final DataListener dataListener;
		private boolean shouldStop = false;
		private StringBuilder buffer = new StringBuilder();
		private InputStream in;
		private long lastReceiveTime=0; // Le serveur n'envoie pas de caractère de fin de ligne. On doit donc détecter une fin de transmission... par le temps.

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
						lastReceiveTime=System.currentTimeMillis();
					}

					// No data for some time, and something to send -> send to listener, and clean cache
					if (lastReceiveTime!=0 && System.currentTimeMillis()>lastReceiveTime+ TIME_TO_WAIT_BEFORE_SENDING_DATA_MSEC) {
						dataListener.newLine(buffer.toString());
						buffer = new StringBuilder();
						lastReceiveTime = 0;
					}
					Thread.sleep(10); // Avoid DoS on server ;-)
				}
				catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		}

		/** Clean stop */
		public void stop() {
			shouldStop = true;
		}
	}


}
