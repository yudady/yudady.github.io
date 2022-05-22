package io.github.bonigarcia.webdriver.jupiter.ch06.selenoid;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;


import static org.assertj.core.api.Assertions.assertThat;

class DockerChromeVncTest {

    WebDriver driver;

    WebDriverManager wdm = WebDriverManager.chromedriver().browserInDocker()
            .enableVnc();

    @BeforeEach
    void setupTest() {
        driver = wdm.create();
    }

    @AfterEach
    void teardown() {
        wdm.quit();
    }

    @Test
    void test() throws Exception {
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        assertThat(driver.getTitle()).contains("Selenium WebDriver");

        // Verify URL for remote session
        URL noVncUrl = wdm.getDockerNoVncUrl();
        assertThat(noVncUrl).isNotNull();

        // Pause for manual inspection
        Thread.sleep(Duration.ofSeconds(60).toMillis());
    }

}