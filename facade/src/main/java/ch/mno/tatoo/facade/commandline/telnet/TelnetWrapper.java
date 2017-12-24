package ch.mno.tatoo.facade.commandline.telnet;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * High-level telnet facade. Will keep data between two server calls (in lastData). Provides waitFor(partString).
 * Created by dutoitc on 11/03/15.
 */
public class TelnetWrapper implements DataListener {
	private Logger LOG = LoggerFactory.getLogger(TelnetWrapper.class);

	private TelnetFacade facade;
	private StringBuffer sb = new StringBuffer();
	private String lastData = "";
	private final int TIMEOUT = 10000;


	public TelnetWrapper(String server, int port) {
		facade = new TelnetFacade(server, port, this);
		facade.start();
		facade.serve();
	}

	@Override
	public void newLine(String line) {
		sb.append(line);
		lastData += line;
	}

	public void stop() {
		if (facade != null) {
			facade.stop();
		}
	}

	public void send(String line) {
		//if (!line.startsWith("getCommandStatus"))
		lastData = "";
		facade.send(line);
	}

	public String getLastData() {
		return lastData;
	}

	public String getAllData() {
		return sb.toString();
	}

	public String waitFor(String... keywords) {
		return waitFor(TIMEOUT, keywords);
	}

	public String waitFor(int timeout, String... keywords) {
		LOG.info("Waiting for " + StringUtils.join(keywords, ','));
		long tMax = System.currentTimeMillis() + timeout;
		int lastLine=0;
		while (true) {
			String[] lines = lastData.split("\n");
			List<String> linesList = Arrays.asList(lines);
			linesList.stream().skip(lastLine).forEach(l -> LOG.debug("  got> " + l));
			lastLine=lines.length;

			for (String lineIt : lines) {
				for (String keyword: keywords) {
					if (lineIt.contains(keyword)) {
						return lineIt;
					}
				}
			}

			try {
				if (System.currentTimeMillis() > tMax) {
					throw new TimeoutException("Timeout while waiting for " + Arrays.toString(keywords) + ". lastData was " + lastData);
				}
				Thread.sleep(500); // TODO: 50
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
