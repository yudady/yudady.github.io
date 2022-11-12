---
title: chromeDrive
author: tommy
tags:
  - selenium
categories:
  - devops
toc: true
date: 2018-10-04 15:31:11
---

# 簡介

- [W3C-WebDriver](https://w3c.github.io/webdriver/)，標準
- [ChromeDriver](http://chromedriver.chromium.org/)
- [Chrome WebDriver commands](https://chromium.googlesource.com/chromium/src/+/master/docs/chromedriver_status.md)
- [selenium](https://github.com/SeleniumHQ/selenium/wiki/JsonWireProtocol)





<!--more-->
# 內容

## selenium 主要調用的類
- org.openqa.selenium.chrome.ChromeDriver
- org.openqa.selenium.chrome.ChromeDriverService
- org.openqa.selenium.remote.RemoteWebDriver


## 可以調用自己寫的js
- org.openqa.selenium.remote.RemoteWebDriver.executeScript(String, Object[])

```java
WebDriver driver = new AnyDriverYouWant();
if (driver instanceof JavascriptExecutor) {
    ((JavascriptExecutor)driver).executeScript("yourScript();");
} else {
    throw new IllegalStateException("This driver does not support JavaScript!");
}
```

```java
driver.get("http://yahoo.com");
  // driver.manage().window().maximize();
  TimeUnit.SECONDS.sleep(2);
  ((JavascriptExecutor) driver).executeScript("window.open()");
  TimeUnit.SECONDS.sleep(2);
  ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
  driver.switchTo().window(tabs.get(1));
  driver.get("https://www.google.com/");

  TimeUnit.SECONDS.sleep(2);
  ((JavascriptExecutor) driver).executeScript("window.open()");
  // 这个不能用
  // driver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL,Keys.RETURN));

  TimeUnit.SECONDS.sleep(2);
  tabs = new ArrayList<String>(driver.getWindowHandles());
  driver.switchTo().window(tabs.get(2));
  driver.get("http://www.baidu.com/");

  TimeUnit.SECONDS.sleep(2);
  driver.switchTo().window(tabs.get(0));

  TimeUnit.SECONDS.sleep(2);
  driver.switchTo().window(tabs.get(1));

  TimeUnit.SECONDS.sleep(2);
  driver.switchTo().window(tabs.get(2));
  TimeUnit.SECONDS.sleep(2);
  driver.switchTo().window(tabs.get(0));
  TimeUnit.SECONDS.sleep(2);
```




## 可以產生圖片
- org.openqa.selenium.remote.RemoteWebDriver.getScreenshotAs(OutputType<X>)
```java
	public static void getFile() throws Exception {

		WebElement Image = driver.findElement(By.cssSelector("#form1 > ul > li:nth-child(3) > img"));
		log.info(Image.getTagName());
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		int width = Image.getSize().getWidth();
		int height = Image.getSize().getHeight();
		log.info(width);
		log.info(height);

		BufferedImage img = ImageIO.read(screen);
		BufferedImage dest = img.getSubimage(Image.getLocation().getX(), Image.getLocation().getY(), width,
			height);
		ImageIO.write(dest, "png", screen);
		File file = new File(image, new Date().getTime() + ".png");
		FileUtils.copyFile(screen, file);

	}

```


## chrome有一個headless，可以隱藏brower
```java
		String chromeDriverPath = userDir + "/src/main/resources/chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--lang=es");
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		driver = new ChromeDriver(options);

```



## appium
> 可以用來測試手機



# 參考資料
[How to run Appium/Selenium test android chrome browser](http://learn-automation.com/execute-appium-selenium-test-android-chrome-browser/)

