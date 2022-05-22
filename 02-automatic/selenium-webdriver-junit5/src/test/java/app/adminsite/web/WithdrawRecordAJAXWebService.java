package app.adminsite.web;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverConditions.title;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.attributeToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;

import java.time.Duration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class WithdrawRecordAJAXWebService {
    String target = "https://pf2admin.uenvsit.com/#/";

    WebDriver driver;
    WebDriverWait wait;
    Actions actions;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.edgedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        driver = new EdgeDriver();

    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void search() throws InterruptedException {

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
        login();

        System.out.println("1==============================");
        WebElement 后台管理 = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.linkText("后台管理"))));
        System.out.println("后台管理.getText() = " + 后台管理.getText());

//        WebElement 后台管理span = findByCssAndWait("body > div > div > div > div > div.headerbox > div > div.logobox > span");
//        System.out.println(后台管理span.getText());
//        WebElement 会员管理 = findByXPathAndWait("/html/body/div/div/div/div/div[2]/div[1]/div/div/ul/li[1]/div[2]/div[3]/span");
        System.out.println("2==============================");
//        会员管理.shouldBe(visible);
//        System.out.println(会员管理.getText());
//
//        会员管理.click();
//        System.out.println("会员管理 = " + 会员管理);
//
//        WebElement 纪录查询 = findByXPathAndWait("/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/ul/li[1]/ul/li[3]/div[1]");
//        纪录查询.click();
//        System.out.println("纪录查询 = " + 纪录查询);
//
//
//        WebElement 提现纪录查询 = findByXPathAndWait("/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/ul/li[1]/ul/li[3]/ul/li[4]/a");
//        提现纪录查询.click();
//        System.out.println("提现纪录查询 = " + 提现纪录查询);
//
//        Thread.sleep(Duration.ofSeconds(3).toMillis());

    }

    private WebElement findByCssAndWait(String cssSelector) {
        return wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector(cssSelector))));
    }

    private WebElement findByXPathAndWait(String xpath) {
        return wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpath))));
    }

    private void login() throws InterruptedException {
        driver.get(target);
        WebElement operatorName = driver.findElement(By.xpath("/html/body/div/div/div/div[3]/div[2]/div[1]/input"));
        WebElement password = driver.findElement(By.xpath("/html/body/div/div/div/div[3]/div[2]/div[2]/input"));
        WebElement submit = driver.findElement(By.xpath("/html/body/div/div/div/div[3]/div[2]/div[3]/ul/li[2]/button/span"));
        operatorName.sendKeys("tommyyyy");
        password.sendKeys("1qaz2wsx");

        //
        submit.click();
//        submit.submit();
//        driver.switchTo();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        WebElement 后台管理 = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
                By.xpath("/html/body/div")))
        );
        System.out.println(" ============  . 后台管理.getText() = " + 后台管理.getText());

//        wait.until(ExpectedConditions.textToBe(By.className("screen"), ));
//        wait.until(ExpectedConditions.visibilityOf(By.xpath("/html/body/div/div/div/div/div[2]/div[1]/div/div/ul/li[1]/div[2]/div[3]/span"))));

//        actions.wait(Duration.ofSeconds(5).toMillis());
//        actions.moveToElement(submit).clickAndHold(driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[2]/div[1]/div/div/ul/li[1]/div[1]")));
//        Thread.sleep(Duration.ofSeconds(5).toMillis());
//        driver.switchTo();


//        open("https://www.google.com/");

//        searchButton.setValue("Selenium").pressEnter();

//        $(byText("Selenium")).shouldBe(visible);


    }

}
