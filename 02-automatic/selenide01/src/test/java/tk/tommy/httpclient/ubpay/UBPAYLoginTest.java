package tk.tommy.httpclient.ubpay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UBPAYLoginTest {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static CookieManager cookieHandler = new CookieManager(null, CookiePolicy.ACCEPT_ALL);


    @Test
    public void login() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(cookieHandler)
                .build();

        String body = """
                {"username": "tommyy", "password": "1qaz2wsx"}
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.payub8demo.com/ajax/auth"))
                .version(HttpClient.Version.HTTP_2)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.getBytes(StandardCharsets.UTF_8)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response = " + response);
        System.out.println("body = " + response.body());
        assertThat(response.statusCode()).isEqualTo(200);

        String cookie = response.headers().firstValue("Set-Cookie").get();
        System.out.println("cookie = " + cookie);


        HttpResponse<String> orderResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create("https://www.payub8demo.com/ajax/order"))
                .GET()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build(), HttpResponse.BodyHandlers.ofString());

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        System.out.println("response = " + orderResponse);
        System.out.println("body = " + orderResponse.body());


    }

}