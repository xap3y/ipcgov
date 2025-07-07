package eu.xap3y.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrowserType {
    CHROME("drivers/chromedriver"),
    FIREFOX("drivers/geckodriver"),
    EDGE("");

    private final String driverPath;
}

