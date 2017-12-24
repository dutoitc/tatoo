package ch.mno.tatoo.facade.karaf.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.mno.tatoo.facade.karaf.data.Feature;

/**
 * Created by dutoitc on 17/08/15.
 */
public class FeaturesListURLCommand extends AbstractCommand {


	public FeaturesListURLCommand() {
		super("features:listurl", 10000, 300);
	}

	public List<Feature> extractFeaturesList() {
		String res = this.getResult();
		int p = res.indexOf(" Loaded   URI");
		int p2 = res.indexOf("karaf@trun>", p);
		if (p2>p) res = res.substring(p, p2);

		Pattern PAT = Pattern.compile("((true|false)) *(.*)");
		Matcher matcher = PAT.matcher(res);

		List<Feature> features = new ArrayList<>();
		while (matcher.find()) {
			String active = matcher.group(2);
			String url = matcher.group(3);
			features.add(new Feature(Boolean.valueOf(active), url));
		}
		return features;
	}


}
