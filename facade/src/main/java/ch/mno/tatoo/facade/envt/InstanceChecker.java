package ch.mno.tatoo.facade.envt;

import ch.mno.tatoo.facade.connectors.ConnectorException;
import ch.mno.tatoo.facade.connectors.HttpConnector;
import org.apache.commons.lang.StringUtils;

/**
 * Created by dutoitc on 04/03/16.
 */
public class InstanceChecker {

    private final EnvironmentReport report;

    public InstanceChecker(EnvironmentReport report) {
        this.report = report;
    }

    public void check(String serverDS, String serverMDM, String tacSuffix) {
        testPage(serverDS, 56751, "/"+tacSuffix+"/?src=InstanceChecker", "tac", "doctype");
        testPage(serverDS, 8040, "/system/console/bundles/?src=InstanceChecker", "karaf", "Error 401:Unauthorized");
        testPage(serverDS, 8081, "/nexus?src=InstanceChecker", "Nexus", "DOCTYPE");
//        testPage(serverMDM, 8180, "/general/secure/?src=InstanceChecker", "MDM", "DOCTYPE");
        testPage(serverMDM, 8180, "/?src=InstanceChecker", "MDM", "DOCTYPE");
//        testPage(serverDS, 8888, "/", "JobServer", null);
    }

    private void testPage(String server, int port, String uri, String label, String shouldContain) {
        HttpConnector conn = new HttpConnector(server, port, "http");
        boolean ok=true;
        try {
            String s = conn.get(uri);
            if (shouldContain!=null && !s.contains(shouldContain)) {
                report.reportInstanceError(label, "Bad response from server for uri=http://" + server + ":" + port + uri);
                ok=false;
            }
        } catch (ConnectorException e) {
            report.reportInstanceError(label, e.getMessage());
            ok=false;
        }
        if (ok) {
            report.reportDetail(StringUtils.rightPad("Instance " + label , 56) + " OK");
        }
    }



}
