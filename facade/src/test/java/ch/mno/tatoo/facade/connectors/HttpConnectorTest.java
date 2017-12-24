package ch.mno.tatoo.facade.connectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dutoitc on 24.12.2017.
 */
public class HttpConnectorTest {

    @Test
    public void test() {
        try(EmbededWebServer srv = new EmbededWebServer()) {
            HttpConnector connector = new HttpConnector("localhost", EmbededWebServer.PORT, "http");
            srv.response="Some response";
            srv.responseCode=200;


            Assert.assertEquals("Some response", connector.get("/test"));

            Map<String, String> values = new HashMap<>();
            values.put("key1", "value1");
            values.put("key2", "value2");
            String res = connector.post("/test", values);
            Assert.assertEquals("Some response", res);
            Assert.assertEquals("key1=value1&key2=value2", srv.buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class EmbededWebServer implements HttpHandler, AutoCloseable {

        private  HttpServer server;
        private static final int PORT = 65481;
        private String response="Dummy response";
        private String buffer;
        private int responseCode=200;

        public EmbededWebServer () throws IOException {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/test", this);
            server.setExecutor(null); // creates a default executor
            server.start();
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            buffer = IOUtils.toString(t.getRequestBody());
            t.sendResponseHeaders(responseCode, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        @Override
        public void close() throws Exception {
            server.stop(0);
        }
    }

}
