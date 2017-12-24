package ch.mno.tatoo.facade.envt;

import ch.mno.tatoo.facade.connectors.ConnectorException;
import ch.mno.tatoo.facade.connectors.HttpConnector;
import ch.mno.tatoo.facade.connectors.JdbcConnector;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by dutoitc on 03/03/16.
 */
public class ServiceChecker {

    private static String query = "select featurename, jobname, status, ERRORSTATUS, APPLICATIONTYPE, errorstacktrace from project_tac.EXECUTIONTASK  where applicationtype is not null order by applicationtype, featurename";
    private final List<String> blacklist;

    private String dbUri, dbUser, dbPass, prefix, serverDS;
    private EnvironmentReport report;
    private String env;

    public ServiceChecker(String dbUri, String dbUser, String dbPass, String prefix, String serverDS, String env, EnvironmentReport report, List<String> blacklist) throws IOException {
        this.dbUri = dbUri;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.prefix = prefix;
        this.serverDS = serverDS;
        this.env = env;
        this.report = report;
        this.blacklist = blacklist;
    }



    public void checkServices(boolean quiet) throws ConnectorException, IOException {
        try (JdbcConnector dbConn = new JdbcConnector(dbUri, dbUser, dbPass)) {
            List<List<String>> dbRes = dbConn.query(query);
            dbRes.stream()
                    .filter(v->{
                        if (blacklist.contains(v.get(0)) || blacklist.contains(v.get(1))) return false;
                        for (String b: blacklist) {
                            if (b.length()<2) continue;
                            if (v.get(0)!=null && v.get(0).contains(b)) return false;
                            if (v.get(1)!=null && v.get(1).contains(b)) return false;
                        }
                        return true;
                    })
                    .forEach(values -> {
                String featureName = values.get(0);
                String jobName = values.get(1);
                String status = values.get(2);
                String errorStatus = values.get(3);
                String applicationType = values.get(4);
                String errorStackTrace = values.get(5);


                switch (applicationType) {
                    case "SERVICE":
                        checkService(featureName, status, errorStatus, errorStackTrace, quiet);
                        break;
                    case "ROUTE":
                        checkRoute(featureName, status, errorStatus, errorStackTrace, quiet);
                        break;
                    default:
                        // Ignored
                        break;
                }
            });
        } catch (Exception e) {
            if (!quiet) e.printStackTrace(System.err);
        }
    }


    public void check(boolean quiet) throws ConnectorException, IOException {
        try (JdbcConnector dbConn = new JdbcConnector(dbUri, dbUser, dbPass)) {
            List<List<String>> dbRes = dbConn.query(query);
            dbRes.forEach(values -> {
                String featureName = values.get(0);
                String jobName = values.get(1);
                String status = values.get(2);
                String errorStatus = values.get(3);
                String applicationType = values.get(4);
                String errorStackTrace = values.get(5);

                switch (applicationType) {
                    case "SERVICE":
                        checkService(featureName, status, errorStatus, errorStackTrace, quiet);
                        break;
                    case "ROUTE":
                        checkRoute(featureName, status, errorStatus, errorStackTrace, quiet);
                        break;
                    case "JOB":
                        checkJob(jobName, status, errorStatus, errorStackTrace);
                        break;
                    case "APPLICATIONTYPE":
                        break; // Header
                    default:
                        System.err.println("Unsupported applicationType: " + applicationType);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkJob(String jobName, String status, String errorStatus, String errorStackTrace) {
        StringBuilder sb = new StringBuilder();
        sb.append("Job ");
        sb.append(StringUtils.rightPad(jobName, 50));
        sb.append(' ');
        sb.append(StringUtils.rightPad(status, 15));
        sb.append(' ');
        if (!errorStatus.equals("NO_ERROR") && !errorStatus.equals("KILLED")) {
            sb.append(errorStatus);
            sb.append(' ');
            report.reportJobErrors(jobName, errorStatus, errorStackTrace);
        }
        report.reportDetail(sb.toString() + "\n");
    }

    private void checkRoute(String featureName, String status, String errorStatus, String errorStackTrace, boolean quiet) {
        StringBuilder sb = new StringBuilder();
        sb.append("Route ");
        sb.append(StringUtils.rightPad(featureName, 50));
        sb.append(' ');
        sb.append(StringUtils.rightPad(status, 15));
        sb.append(' ');
        if (!errorStatus.equals("NO_ERROR")) {
            sb.append(errorStatus);
            sb.append(' ');
            report.reportRouteErrors(featureName, errorStatus, errorStackTrace);
        }

        if (featureName.contains("TatooURI")) {
//            System.out.println(featureName);

            HttpConnector conn8040 = new HttpConnector(serverDS, 8040, "http");
            HttpConnector conn8048 = new HttpConnector(serverDS, 8048, "http");

            sb = new StringBuilder();
            sb.append("Route ");
            sb.append(StringUtils.rightPad(featureName, 50));
            sb.append(' ');
            sb.append(StringUtils.rightPad(status, 15));
            sb.append(' ');

            // Deployed services should be accessible
            if (status.equals("DEPLOYED") || status.equals("UNDEPLOYED")) {

                if (containsWADL(featureName, "", conn8040, quiet)) {
                    sb.append("WADL8040_OK ");
                } else {
                    sb.append("MissingWADL8040 ");
                    report.reportServiceMissingWADL8040(featureName);
                }
            }
        }

        report.reportDetail(sb.toString());
    }

    private void checkService(String featureName, String status, String errorStatus, String errorStackTrace, boolean quiet) {
        HttpConnector conn8040 = new HttpConnector(serverDS, 8040, "http");
        HttpConnector conn8048 = new HttpConnector(serverDS, 8048, "http");

        StringBuilder sb = new StringBuilder();
        sb.append("Service ");
        sb.append(StringUtils.rightPad(featureName, 50));
        sb.append(' ');
        sb.append(StringUtils.rightPad(status, 15));
        sb.append(' ');

        // Deployed services should be accessible
        if (status.equals("DEPLOYED") || status.equals("UNDEPLOYED")) {
            if (featureName.startsWith("WS_")) {

                if (containsWADL(featureName, "", conn8040, quiet)) {
                    sb.append("WADL8040_OK ");
                } else {
                    sb.append("MissingWADL8040 ");
                    report.reportServiceMissingWADL8040(featureName);
                }
                if (containsWADL(featureName, "/mycontext/" + prefix + "project", conn8048, quiet)) {
                    sb.append("WADL8048_OK ");
                } else {
                    sb.append("MissingWADL8048 ");
                    report.reportServiceMissingWADL8048(featureName);
                }
            } else {
                if (containsWSDL(featureName.replace("-feature", ""), conn8040, quiet)
                        || containsWSDL(featureName.replace("-feature", "") + "Operation", conn8040, quiet)
                        || containsWSDL(featureName.replace("-feature", "") + "Service", conn8040, quiet)
                        || containsWSDL(featureName.replace("-feature", "") + "2", conn8040, quiet)) {
                    sb.append("WSDL_OK ");
                } else {
                    sb.append("MissingWSDL ");
                    report.reportServiceMissingWSDL(featureName);
                }
            }
        }
        report.reportDetail(sb.toString());
    }

    private static boolean containsWSDL(String featureName, HttpConnector conn, boolean quiet) {
        try {
            String uri = "/services/" + featureName + "?wsdl";
            String ret = conn.get(uri);
            if (!ret.contains("wsdl:definitions")) {
                return false;
            }
            return true;
        } catch (ConnectorException e) {
            if (!quiet) e.printStackTrace(System.err);
            return false;
        }
    }

    private boolean containsWADL(String featureName, String prefix, HttpConnector conn, boolean quiet) {
        try {
            // FIXME: extract version from service feature name
            String serviceName = String.valueOf(featureName.charAt(3)).toLowerCase() + featureName.substring(4).replace("-feature", "");
            String version = "v1";
            if (serviceName.endsWith("_V3") || serviceName.endsWith("v3")) {
                serviceName = serviceName.substring(0, serviceName.length() - 3);
                version = "v3";
            }

            String uri = prefix + "/services/" + version + "/" + serviceName + "?_wadl";
            String ret = conn.get(uri);
            if (!ret.contains("<application")) {
                System.err.println("KO:" + uri);
                return false;
            }
            return true;
        } catch (ConnectorException e) {
            if (!quiet) e.printStackTrace(System.err);
            return false;
        }
    }


}
