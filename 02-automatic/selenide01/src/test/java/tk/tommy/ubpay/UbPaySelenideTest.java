package tk.tommy.ubpay;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import tk.tommy.ubpay.config.OrderConfigResponse;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class UbPaySelenideTest {

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @Test
    public void selenide01() throws Exception {
        open("https://127.0.0.1/config");

        SelenideElement pre = $(By.cssSelector("pre"));
        String json = pre.getText();
        System.out.println(json);


        pr(json, OrderConfigResponse.class);
    }


    private <T> void pr(String json, Class<T> clazz) {
        Gson gson = new Gson();
        var obj = gson.fromJson(json, clazz);
        ReflectionToStringBuilder.toString(obj, ToStringStyle.MULTI_LINE_STYLE, true, true);
    }
}
