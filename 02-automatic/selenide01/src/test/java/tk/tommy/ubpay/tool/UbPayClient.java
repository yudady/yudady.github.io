package tk.tommy.ubpay.tool;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class UbPayClient {

    public static HttpClient getUbPayClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLParameters sslParams = new SSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("");
        SSLContext sc = SSLContext.getInstance("SSL");
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");//取消主机名验证
        sc.init(null, getTrustManagers(), new SecureRandom());
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(60 * 1000))
                .sslContext(sc)
                .sslParameters(sslParams)
                .build();
        return httpClient;
    }


    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null; // Not relevant.
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub
            }
        }};

    }

}
