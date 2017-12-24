package ch.mno.tatoo.facade.connectors;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A connector to ease http operations
 * Created by dutoitc on 30.01.2016.
 */
public class HttpConnector extends AbstractConnector implements AutoCloseable {

    private CloseableHttpClient httpclient;
    private HttpHost target;

    public HttpConnector(String hostname, int port, String scheme) {
        this(hostname, port, scheme, null, -1, null, 20*1000);
    }

    public HttpConnector(String hostname, int port, String scheme, String proxyHostname, int proxyPort, String proxyScheme, int timeout) {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(timeout);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
        requestBuilder.setSocketTimeout(timeout);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
        if (proxyHostname!=null) {
            HttpHost proxy = new HttpHost(proxyHostname, proxyPort, proxyScheme);
            httpClientBuilder.setProxy(proxy);
        }
        httpclient = httpClientBuilder.build();
        target = new HttpHost(hostname, port, scheme);
    }

    public String post(String uri, Map<String, String> values) throws ConnectorException {
        HttpPost post = new HttpPost(uri);
        final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry: values.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        post.setEntity(new UrlEncodedFormEntity(nvps, Charset.defaultCharset()));
//        post.setConfig(config);

        return query(post);
    }

    private String query(HttpRequest request) throws ConnectorException {
        try (CloseableHttpResponse response = httpclient.execute(target, request)){
            if (response.getStatusLine().getStatusCode()!=200) {
                return "Error " + response.getStatusLine().getStatusCode() + ":" + response.getStatusLine().getReasonPhrase();
            }
            return EntityUtils.toString(response.getEntity()).trim();
        } catch (SocketTimeoutException e) {
            throw new ConnectorException("Exception: " + e.getMessage() + " on " + request.getRequestLine().getUri(), e);
        } catch (IOException e) {
            throw new ConnectorException("Exception: " + e.getMessage(), e);
        }

    }

    public String get(String uri) throws ConnectorException {
        HttpGet request = new HttpGet(uri);
        return query(request);
    }

    public void close() {
        try {
            httpclient.close();
        } catch (IOException e) {
        }
    }


}
