package ch.mno.tatoo.facade.bonita;

/**
 * Work under progress
 */
public class Poc2 {
//
//        public static void main(String[] args) throws Exception {
////            //instantiateProcess method
////            //the given process must be deployed before
////            System.out.println("--------------\nCreating a new process instance (variables with default values)\n--------------\n");
////            String url = "http://localhost:8080/bonita-server-rest/API/runtimeAPI/instantiateProcess/myProcess--1.0";
////            String parameters = "options=user:john";
////            HttpURLConnection connection = getConnection(url, parameters);
////            processResponse(connection);
////
////            //instantiateProcessWithVariables
////            System.out.println("\n--------------\nCreating a new process instance (variables with initialized values)\n--------------\n");
////            url = "http://localhost:8080/bonita-server-rest/API/runtimeAPI/instantiateProcessWithVariables/myProcess--1.0";
////            String xmlVariables="<map><entry><string>globalVar</string><string>new value</string></entry></map>";
////            xmlVariables = URLEncoder.encode(xmlVariables, "UTF-8");
////            parameters = "options=user:john&variables="+xmlVariables;
////            connection = getConnection(url, parameters);
////            processResponse(connection);
//
//            System.out.println("\n--------------\nRetriving all process instances ...\n--------------\n");
//            //get light process instances
//            //url = "http://localhost:8080/bonita-server-rest/API/queryRuntimeAPI/getProcessInstances";
//            //String url="http://hostname:8080/bonita-server-rest/API/queryRuntimeAPI/getProcessInstances";
//            String url="http://hostname:8080/bonita-server-rest/API/queryDefinitionAPI/getLightProcesses";
//            //String parameters = "options=user:john";
////            String parameters = "options=user:username";
//            String parameters = "options=user:restuser";
//            HttpURLConnection connection = getConnection(url, parameters);
//            processResponse(connection);
//        }
//
//        private static HttpURLConnection getConnection(final String url, final String parameters) throws IOException,
//                MalformedURLException, ProtocolException {
//            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//            connection.setUseCaches (false);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setInstanceFollowRedirects(false);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes("restuser:restbpm".getBytes()));
//            //connection.getHeaderFields().put("options", Arrays.asList("restuser:restbpm"));
//
//            final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
//            output.writeBytes(parameters);
//            output.flush();
//            output.close();
//            connection.disconnect();
//
//            return connection;
//        }
//
//        /**
//         * @param connection
//         * @throws IOException
//         */
//        private static void processResponse(HttpURLConnection connection)
//                throws IOException {
//            int responseCode = connection.getResponseCode();
//            if(responseCode != HttpURLConnection.HTTP_OK){
//                System.out.println("----------\nRequest failled: " + responseCode+ "\n----------");
//            } else {
//                System.out.println("----------\nResponse content: \n----------");
//                final InputStream is = connection.getInputStream();
//                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                String line;
//                StringBuffer response = new StringBuffer();
//                try {
//                    while((line = reader.readLine()) != null) {
//                        response.append(line);
//                        response.append('\n');
//                    }
//                } finally {
//                    reader.close();
//                    is.close();
//                }
//                System.out.println(response.toString().trim());
//            }
//        }

}
