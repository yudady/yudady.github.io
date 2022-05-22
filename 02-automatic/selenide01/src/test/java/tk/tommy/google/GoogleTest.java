package tk.tommy.google;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;


import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class GoogleTest {
    public SelenideElement searchButton = $("body > div.L3eUgb > div.o3j99.ikrT4e.om7nvf > form > div:nth-child(1) > div.A8SBwf > div.RNNXgb > div > div.a4bIc > input");

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @Test
    public void search() {
        open("https://www.google.com/");

        searchButton.setValue("Selenium").pressEnter();

        $(byText("Selenium")).shouldBe(visible);

        SelenideElement $ = $(By.xpath("//*[@id=\"rso\"]/div[1]/div/div/div[1]/a/h3"));
        $.shouldBe(visible);
        System.out.println($.getText());

    }


}
