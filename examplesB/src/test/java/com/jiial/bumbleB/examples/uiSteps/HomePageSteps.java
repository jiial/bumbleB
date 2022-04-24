package com.jiial.bumbleB.examples.uiSteps;

import com.jiial.bumbleB.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class HomePageSteps {

    private final static String HOME_PAGE_URL = "https://www.helsinki.fi/en";
    private final WebDriver driver;

    public HomePageSteps() {
        System.setProperty("webdriver.edge.driver", System.getenv("driver"));
        driver = new EdgeDriver();
        driver.get(HOME_PAGE_URL);
    }

    @Step("the front page is shown")
    public void frontPageIsShown() {
        assertThat(driver.getCurrentUrl().equals(HOME_PAGE_URL)).isTrue();
    }

    @Step("the page contains text {text}")
    public void pageContains(String text) {
        assertThat(driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]")).isEmpty()).isFalse();
    }

    public WebDriver getDriver() {
        return driver;
    }
}
