package ch.mno.tatoo.facade.karaf.data;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dutoitc on 22/11/17.
 */
public class KarafElementTest {

    @Test
    public void testIsTatooForNonTatooMaven() {
        testIsTatoo("mvn:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_5", false);
    }

    @Test
    public void testIsTatooForNonTatooNonMaven() {
        testIsTatoo("spring:org.apache.servicemix.bundles/org.apache.servicemix.bundles.dom4j/1.6.1_5", false);
    }

    @Test
    public void testIsTatooForTatooWithoutPackage() {
        testIsTatoo("mvn:E_Achat.F_Common/BuildAndStoreVolubility-control-bundle/1.23.0-SNAPSHOT", true);
    }

    @Test
    public void testIsTatooForTatooFull() {
        testIsTatoo("mvn:ch.mno.dummyapp.job.E_Achat.F_Common/BuildAndStoreVulcanData-control-bundle/1.23.0-SNAPSHOT", true);
    }

    private void testIsTatoo(String location, boolean expected) {
        KarafElement el = new KarafElement();
        el.setLocation(location);
        Assert.assertEquals(expected, el.isBaseGroupId("ch.mno.dummyapp"));
    }

}
