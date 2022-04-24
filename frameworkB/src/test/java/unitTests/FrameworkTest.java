package unitTests;

import com.jiial.bumbleB.framework.Framework;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FrameworkTest {

    @Test
    public void scenarioDefinitionEmpty() {
        Framework.ExampleDefinition definition = new Framework.ExampleDefinition.ExampleBuilder().build();
        assertThat(definition.getSteps().isEmpty()).isTrue();
    }
}
