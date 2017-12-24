package ch.mno.tatoo.facade.nexus;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extractor to find subpages and maven-metadata.xml based on URL. Add found results as instance data. Should be used in an executor.
 * Created by dutoitc on 18/08/15.
 */
public class NexusMetadataExtractor implements Runnable {

	private final Pattern PAT_METADATA = Pattern.compile("<a href=\"(.*?maven-metadata.xml)\">");

	private String url;
	private List<String> metadata;
	private List<String> subpages;
	private boolean finished;

	public void setUrl(String url) {
		this.url = url;
		metadata = new ArrayList<>();
		subpages = new ArrayList<>();
		finished = false;
	}

	public boolean isFinished() {
		return finished;
	}

	public List<String> getMetadata() {
		return metadata;
	}

	public List<String> getSubpages() {
		return subpages;
	}

	@Override
	public void run() {
		String page = null;
		try {
			page = IOUtils.toString(new URI(url));


			// Find all url to maven-metadata.xml
			Matcher matchMetadata = PAT_METADATA.matcher(page);
			while (matchMetadata.find()) {
				String metaURL = matchMetadata.group(1);
				metadata.add(metaURL);
			}

			// Find subpages
			String pat1 = "<a href=\"(";
			String pat2 = url.replace("/", "\\/");
			String pat3 = ".+?/)\""; // The slash is important, or all files will match
			Pattern patSubpages = Pattern.compile(pat1 + pat2 + pat3);
			Matcher matchSubpages = patSubpages.matcher(page);
			while (matchSubpages.find()) {
				subpages.add(matchSubpages.group(1));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		finished = true;
	}

}
