package eu.xap3y.util;

import eu.xap3y.Main;
import eu.xap3y.enums.BrowserType;
import lombok.SneakyThrows;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    @SneakyThrows
    public static WebDriver getDriver(BrowserType browserType) {
        //String driverPath = Utils.extractDriverToTemp(browserType.getDriverPath());
        String driverPath = browserType.getDriverPath();

        switch (browserType) {
            case CHROME:
                System.setProperty("webdriver.chrome.driver", driverPath);
                break;
            case FIREFOX:
                System.setProperty("webdriver.gecko.driver", driverPath);
                break;
            case EDGE:
                System.setProperty("webdriver.edge.driver", driverPath);
                break;
        }

        WebDriver driver;
        switch (browserType) {
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setBinary("drivers/chromedriver");
                if (Main.RUN_HEADLESS) chromeOptions.addArguments("--headless");
                driver = new ChromeDriver(chromeOptions);
                break;
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setBinary("/usr/bin/firefox");
                if (Main.RUN_HEADLESS) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setBinary("");
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser type");
        }
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;
    }
}
