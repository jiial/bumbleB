package com.jiial.bumbleB.examples.uiTests;

import com.jiial.bumbleB.annotations.Example;
import com.jiial.bumbleB.examples.uiSteps.HomePageSteps;
import org.junit.jupiter.api.AfterEach;

import static com.jiial.bumbleB.framework.Framework.*;


public class HomePageTest {

    private final ExampleDefinition.ExampleBuilder builder = new ExampleDefinition.ExampleBuilder();
    private final HomePageSteps homePageSteps = new HomePageSteps();
    private static final String EXPECTED_TEXT = "Spend the summer studying";

    @Example
    public void homePageContainsExpectedText() {
        builder
                .name("The home page contains the expected text")
                .steps(
                        given(homePageSteps::frontPageIsShown),
                        then(homePageSteps::pageContains, EXPECTED_TEXT)
                )
                .build()
                .run();
    }

    @AfterEach
    public void tearDown() {
        homePageSteps.getDriver().close();
    }
}
