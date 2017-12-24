package ch.mno.tatoo.facade.bonita;


/**
 * Work under progress
 * c.f. http://community.bonitasoft.com/how-use-bonita-http-api
 */
public class poc {


//        private static final String LOGIN = "restuser";
//        private static final String PSSWD = "restbpm";

    private static final String LOGIN = "restuser";
    private static final String PSSWD = "restbpm";
    private static final String LOGIN2 = "admin";
    private static final String PSSWD2 = "talend";

//    private static final String LOGIN = "test";
//    private static final String PSSWD = "test";
        private static final String jaasFile = "src/main/resources/jaas-standard.cfg";
//
//        public static void main(String[] args) throws Exception {
//
//            //set system properties
//            System.setProperty(BonitaConstants.API_TYPE_PROPERTY, "REST");
//            System.setProperty(BonitaConstants.REST_SERVER_ADDRESS_PROPERTY, "hostname:8080/bonita-server-rest/");
//            System.setProperty(BonitaConstants.JAAS_PROPERTY, jaasFile);
//
//            // c.f. ClientLoginModule ?
//
//            //login
//            //verify the user exists
//            //LoginContext loginContext = new LoginContext("BonitaAuth",
//            //        new SimpleCallbackHandler("restuser", "restbpm"));
//            //new SimpleCallbackHandler(LOGIN, PSSWD));
//           /* LoginContext loginContext = new LoginContext("BonitaRESTServer",
//                    new SimpleCallbackHandler("restuser", "restbpm"));
//            loginContext.login();
//            loginContext.logout();*/
//
//            //propagate the user credentials
////            LoginContext loginContext = new LoginContext("BonitaStore",
////                    new SimpleCallbackHandler(LOGIN, PSSWD));
////            loginContext.login();
//
//
//            LoginContext loginContext = new LoginContext("BonitaRESTServer",
//                    new SimpleCallbackHandler("restuser", "restbpm"));
//            //new SimpleCallbackHandler(LOGIN, PSSWD));
//           /* LoginContext loginContext = new LoginContext("BonitaRESTServer",
//                    new SimpleCallbackHandler("restuser", "restbpm"));*/
//            loginContext.login();
//            loginContext.logout();
//
//             loginContext = new LoginContext("BonitaStore",
//                    new SimpleCallbackHandler(LOGIN2, PSSWD2));
//            loginContext.login();
//            //loginContext.logout();
//
//            /* loginContext = new LoginContext("BonitaStore",
//                    new SimpleCallbackHandler(LOGIN, PSSWD));
//            loginContext.login();*/
//            System.out.println(loginContext.getSubject().getPrincipals().iterator().next().getName());
//
//
//            //get he APIs
//            final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
//            final RuntimeAPI runtimeAPI = AccessorUtil.getRuntimeAPI();
//            final QueryRuntimeAPI queryRuntimeAPI = AccessorUtil.getQueryRuntimeAPI();
//
//            //runtimeAPI.
//
//            //System.out.println("DBG1>" + managementAPI.isUserAdmin("test"));
//            managementAPI.checkUserCredentials("restuser", "restbpm");
//
//            for (LightProcessInstance lp:queryRuntimeAPI.getLightProcessInstances()) {
//                System.out.println("- " + lp.getUUID());
//            }
//
//
//            loginContext.logout();
//        }
}
