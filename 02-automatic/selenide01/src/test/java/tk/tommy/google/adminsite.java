package tk.tommy.google;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;

public class adminsite {
    public SelenideElement userNameBtn = $("body > div > div > div > div.loginbox > div.bodybox > div:nth-child(1) > input");
    public SelenideElement passwordBtn = $("body > div > div > div > div.loginbox > div.bodybox > div:nth-child(2) > input");
    public SelenideElement loginBtn = $("body > div > div > div > div.loginbox > div.bodybox > div.btnbox > ul > li:nth-child(2) > button > span");


    @BeforeAll
    public static void setUpAll() {
//        Configuration.browserSize = "1280x800";
        Configuration.startMaximized = true;
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @Test
    public void search() throws InterruptedException {
        open("https://pf2admin.uenvsit.com/#/");
        userNameBtn.setValue("tommyyyy");
        passwordBtn.setValue("1qaz2wsx");
        loginBtn.click();
        TimeUnit.SECONDS.sleep(2);

        // 后台管理员
        SelenideElement $ = getByCss("body > div > div > div > div > div.headerbox > div > div.navbox > div > div.navbarright > h2.usrbtn");
        $.shouldBe(visible);
        System.out.println("后台管理员 $.getText()" + $.getText());
        TimeUnit.SECONDS.sleep(3);

        // 会员管理
        SelenideElement 会员管理 = $(byText("会员管理"));

        会员管理.shouldBe(visible);
        会员管理.shouldHave(text("会员管理"));
        会员管理.click();
        System.out.println("会员管理 click ");
        TimeUnit.SECONDS.sleep(2);

//        // 记录查询
//        $ = getByXPath("/html/body/div/div/div/div/div[2]/div[1]/div/div/ul/li[1]/ul/li[3]/div[1]");
//        $.shouldHave(visible, Duration.ofSeconds(5));
//        System.out.println("记录查询 $.getText()" + $.getText());
//        System.out.println("记录查询 click ");
        TimeUnit.SECONDS.sleep(2);

    }

    private SelenideElement getByCss(String cssSelector) {
        SelenideElement $ = $(cssSelector);
        SelenideElement selenideElement = $.shouldBe(visible);
        return selenideElement;
    }

    private SelenideElement getByXPath(String xpath) {
        SelenideElement $ = $x(xpath);
        SelenideElement selenideElement = $.shouldBe(visible);
        return selenideElement;
    }
}
