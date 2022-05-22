package tk.tommy.ubpay;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class UbPayApiTest {

    private URI uri = URI.create("https://localhost/config");

    public static HttpClient getUbPayClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
        sc.init(null, getTrustManagers(), new SecureRandom());
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(60 * 1000))
                .sslContext(sc)
                .sslParameters(sslParams)
                .build();
    }


    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }};

    }


    @Test
    public void test01() throws Exception {

        HttpClient httpClient = getUbPayClient();
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://localhost/config"))
                .GET()
                .build();
        HttpResponse<String> result = httpClient.send(requestBuilder, HttpResponse.BodyHandlers.ofString());

        System.out.println(result.body());
    }


    private void pr(Object obj) {
        ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE, true, true);

    }
}
