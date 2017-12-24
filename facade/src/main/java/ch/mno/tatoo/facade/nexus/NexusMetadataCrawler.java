package ch.mno.tatoo.facade.nexus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Crawler to find metadata on a nexus URL, recursively
 * Created by dutoitc on 18/08/15.
 */
public class NexusMetadataCrawler {


	public static List<String> findMetadata(String url) throws URISyntaxException, IOException, InterruptedException {
		List<String> metadata = new ArrayList<>();
		List<String> subpages = new ArrayList<>();
		subpages.add(url);
		ExecutorService es = Executors.newFixedThreadPool(10);

		List<NexusMetadataExtractor> crawlers = new ArrayList<>();
		while (!subpages.isEmpty() || !crawlers.isEmpty()) {
//			System.out.println("subpages:" + subpages.size()+" / " + es.isTerminated() + " / crawlers:" + crawlers.size());
			while (!subpages.isEmpty()) {
				String itUrl = subpages.remove(0);
				NexusMetadataExtractor crawler = new NexusMetadataExtractor();
				crawler.setUrl(itUrl);
				es.execute(crawler);
				crawlers.add(crawler);
			}
			Iterator<NexusMetadataExtractor> it = crawlers.iterator();
			while (it.hasNext()) {
				NexusMetadataExtractor itCrawler = it.next();
				if (itCrawler.isFinished()) {
					it.remove();
					metadata.addAll(itCrawler.getMetadata());
					subpages.addAll(itCrawler.getSubpages());
				}
			}

			try {
				Thread.sleep(5);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		es.shutdown();
		return metadata;
	}

}
