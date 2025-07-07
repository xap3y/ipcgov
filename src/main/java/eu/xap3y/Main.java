package eu.xap3y;

import eu.xap3y.enums.BrowserType;
import eu.xap3y.enums.VisaResultType;
import eu.xap3y.util.DriverFactory;
import eu.xap3y.util.Logger;
import eu.xap3y.util.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String URL = "https://ipc.gov.cz/informace-o-stavu-rizeni/";
    private static final String INPUT_XPATH = "/html[1]/body[1]/div[2]/main[1]/div[2]/div[1]/div[2]/form[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]";
    private static final String RESULT_XPATH = "/html[1]/body[1]/div[2]/main[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]";
    private static final String SUBMIT_BUTTON = "/html[1]/body[1]/div[2]/main[1]/div[2]/div[1]/div[2]/form[1]/div[1]/div[3]/button[1]";
    private static final String REJECT_COOKIES = "/html/body/div[2]/main/div[1]/div/div/div/div/div/div/button[2]";
    private static final String ALERT_CONTENT = "/html[1]/body[1]/div[2]/main[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]";
    public static final boolean RUN_HEADLESS = true;

    private static final String PREFIX = "BANG"; // Bangkok czech embassy prefix
    private static final LocalDate START_DATE = LocalDate.of(2025, 6, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 6, 30);

    public static void main(String[] args) {

        Logger log = Logger.getInstance();

        WebDriver driver = DriverFactory.getDriver(BrowserType.FIREFOX);

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        try {
            driver.get(URL);

            Utils.waitForElement(driver, By.xpath(REJECT_COOKIES)).click();

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (LocalDate date = START_DATE; !date.isAfter(END_DATE); date = date.plusDays(1)) {

                int serial = 1;
                boolean foundApproved = true;

                while (foundApproved) {
                    String visaNumber = String.format("%s%s%04d", PREFIX, date.format(dateFmt), serial);
                    VisaResultType typeOfResult = checkVisaNumber(driver, visaNumber);

                    if (typeOfResult == VisaResultType.ALLOWED || typeOfResult == VisaResultType.REJECTED || typeOfResult == VisaResultType.WAITING) {
                        serial++;
                    } else {
                        foundApproved = false;
                    }

                    if (typeOfResult == VisaResultType.ALLOWED) {
                        log.ok(" {}", visaNumber);
                    } else if (typeOfResult == VisaResultType.NOT_FOUND) {
                        //log.info("- {} - NOT_FOUND", visaNumber); // TODO
                        continue;
                    } else if (typeOfResult == VisaResultType.REJECTED) {
                        log.info("❌ {} - rejected", visaNumber);
                    } else if (typeOfResult == VisaResultType.WAITING) {
                        log.info("\uD83D\uDD57 {} - waiting", visaNumber);
                    } else {
                        log.err("❓ {} - unknown result", visaNumber);
                    }
                }
            }
        } finally {
            driver.quit();
            log.close();
        }

    }

    public static VisaResultType checkVisaNumber(WebDriver driver, String visaNumber) {
        try {
            driver.navigate().refresh();
            Thread.sleep(100);
            WebElement input = Utils.waitForElement(driver, By.xpath(INPUT_XPATH));
            input.clear();
            input.sendKeys(visaNumber);
            Utils.waitForElement(driver, By.xpath(SUBMIT_BUTTON)).click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
            WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(RESULT_XPATH)));

            String classAttr = result.getAttribute("class");
            if (classAttr != null && classAttr.contains("alert--form-success")) return VisaResultType.ALLOWED;
            if (classAttr != null && classAttr.contains("alert--form-warning")) return VisaResultType.WAITING;

            Thread.sleep(100);
            WebElement contentDiv = Utils.waitForElement(driver, By.xpath(ALERT_CONTENT));
            String resultText = contentDiv.getText().toLowerCase();

            System.out.println(resultText);

            if (resultText.contains("nebylo nalezeno")) return VisaResultType.NOT_FOUND;

            Thread.sleep(100);
            if (classAttr != null && classAttr.contains("alert--form-error")) return VisaResultType.REJECTED;

            return VisaResultType.UNKNOWN;
        } catch (Exception e) {
            System.err.println("Error checking visa number " + visaNumber + ": " + e.getMessage());
            return VisaResultType.UNKNOWN;
        }
    }
}