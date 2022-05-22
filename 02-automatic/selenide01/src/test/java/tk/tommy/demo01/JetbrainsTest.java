package tk.tommy.demo01;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class JetbrainsTest {
    public SelenideElement seeAllToolsButton = $("a.wt-button_mode_primary");
    public SelenideElement toolsMenu = $x("//div[contains(@class, 'menu-main__item') and text() = 'Tools']");
    public SelenideElement searchButton = $("[data-test=menu-main-icon-search]");

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        open("https://www.jetbrains.com/");
    }

    @Test
    public void search() {
        searchButton.click();

        $("#header-search").sendKeys("Selenium");
        $x("//button[@type='submit' and text()='Search']").click();

        $(".js-search-input").shouldHave(attribute("value", "Selenium"));
    }

    @Test
    public void toolsMenu() {
        toolsMenu.hover();

        $(".menu-main__popup-wrapper").shouldBe(visible);
    }

    @Test
    public void navigationToAllTools() {
        seeAllToolsButton.click();

        $(".products-list").shouldBe(visible);

        assertEquals("All Developer Tools and Products by JetBrains", Selenide.title());
    }
}
