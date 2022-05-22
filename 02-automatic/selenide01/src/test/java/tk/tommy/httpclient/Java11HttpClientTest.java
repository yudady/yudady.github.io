package tk.tommy.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.PasswordAuthentication;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class Java11HttpClientTest {
    private ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        List<String> sampleList = Arrays.asList("Java", "Groovy");
        String language = sampleList.stream()
            .map((@Nonnull var x) -> x.toUpperCase())
            .collect(Collectors.joining(","));

        assertThat(language).isEqualTo("JAVA,GROOVY");
    }

    @Test
    public void test01() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.hao123.com"))
            .build();
        // 非同步
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .join();

        // 同步
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    @Test
    public void createAnHTTPClient() throws NoSuchAlgorithmException {
        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .proxy(ProxySelector.getDefault())
            .followRedirects(HttpClient.Redirect.NEVER)
            .authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("user", "pass".toCharArray());
                }
            })
            .cookieHandler(new CookieManager())
            .executor(Executors.newFixedThreadPool(2))
            .priority(1)
            .sslContext(SSLContext.getDefault())
            .sslParameters(new SSLParameters())
            .connectTimeout(Duration.ofSeconds(1))
            .build();

        assertThat(client.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void createADefaultHTTPClient() {
        HttpClient client = HttpClient.newHttpClient();

        assertThat(client.version()).isEqualTo(HttpClient.Version.HTTP_2);
        assertThat(client.followRedirects()).isEqualTo(HttpClient.Redirect.NEVER);
        assertThat(client.proxy()).isEmpty();
        assertThat(client.connectTimeout()).isEmpty();
        assertThat(client.cookieHandler()).isEmpty();
        assertThat(client.authenticator()).isEmpty();
        assertThat(client.executor()).isEmpty();
    }

    @Test
    public void buildAnHTTPRequest() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .version(HttpClient.Version.HTTP_2)
            .header("Accept", "application/json")
            .timeout(Duration.ofMillis(500))
            .build();

        assertThat(request.timeout().get()).isEqualTo(Duration.ofMillis(500));
    }

    @Test
    public void getSync() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .header("Accept", "application/json")
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void getAsync() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .header("Accept", "application/json")
            .build();

        int statusCode = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApplyAsync(HttpResponse::statusCode)
            .join();

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void postAsync() {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("ping!"))
            .build();

        CompletableFuture<HttpResponse<String>> completableFuture =
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        completableFuture
            .thenApplyAsync(HttpResponse::headers)
            .thenAcceptAsync(System.out::println);
        HttpResponse<String> response = completableFuture.join();

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void postJson() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Book book = new Book(1, "Java HttpClient in practice");
        String body = objectMapper.writeValueAsString(book);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(objectMapper.readValue(response.body(), Book.class).id).isEqualTo(1);
    }

    @Test
    public void basicAuthentication() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String encodedAuth = Base64.getEncoder()
            .encodeToString(("user" + ":" + "pass").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/secure"))
            .header("Authorization", "Basic " + encodedAuth)
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    public void cookie() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/set-cookie"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String cookie = response.headers().firstValue("Set-Cookie").get();
        assertThat(HttpCookie.parse(cookie).get(0).getName()).isEqualTo("SID");

        request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/test/resource"))
            .header("Accept", "application/json")
            .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }


    public class Book {
        public int id;
        public String title;

        public Book() {

        }

        public Book(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

}