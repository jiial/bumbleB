package com.jiial.bumbleB.unitTests;

import com.jiial.bumbleB.framework.Framework;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrameworkTest {

    @Test
    public void scenarioDefinitionEmpty() {
        Framework.ExampleDefinition definition = new Framework.ExampleDefinition.ExampleBuilder().build();
        assertTrue(definition.getSteps().isEmpty());
    }
}
