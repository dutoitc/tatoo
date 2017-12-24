package ch.mno.tatoo.facade.karaf.commands;

import ch.mno.tatoo.facade.karaf.data.Feature;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by dutoitc on 17/08/15.
 */
public class FeaturesListURLCommandTest {

	@Test
	public void testExtract() {
		String s="true    mvn:http://admin:admin123@server:8081/nexus/content/repositories/snapshots!ch.mno.dummyapp.service.F_Achat.E_LITECOIN/EnregistrementInformationsSecretes-feature/1.0.3-SNAPSHOT/xml\n"+
				"true    mvn:http://admin:admin123@server:8081/nexus/content/repositories/snapshots!ch.mno.dummyapp.service.F_Achat.F_BITCOIN/RecupererInfoBitcoinDepuisWeb-feature/1.0.1-SNAPSHOT/xml\n"+
				"true    mvn:http://admin:admin123@server:8081/nexus/content/repositories/releases!ch.mno.dummyapp.service.R_Technique/WS_Infrastructure-feature/2.34.0/xml\n";


		FeaturesListURLCommand cmd = new FeaturesListURLCommand();
		cmd.feed(s);
		List<Feature> features = cmd.extractFeaturesList();
		Assert.assertEquals(3, features.size());
	}

}
