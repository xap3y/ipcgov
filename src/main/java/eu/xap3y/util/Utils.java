package eu.xap3y.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Utils {

    public static WebElement waitForElement(WebDriver driver, By locator, int timeout) {
        WebDriverWait wait = delay(driver, timeout);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElement(WebDriver driver, By locator) {
        return waitForElement(driver, locator, 6);
    }

    public static WebDriverWait delay(WebDriver driver, int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }
}
