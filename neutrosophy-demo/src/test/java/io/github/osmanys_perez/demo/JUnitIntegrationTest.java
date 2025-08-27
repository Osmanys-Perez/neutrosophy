package io.github.osmanys_perez.demo;

import io.github.osmanys_perez.neutrosophy.evaluator.FuzzyStringEvaluator;
import io.github.osmanys_perez.neutrosophy.junit5.NeutrosophicTest;
import io.github.osmanys_perez.neutrosophy.junit5.NeutrosophicAssertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.Test;

public class JUnitIntegrationTest {

    @Test
    @NeutrosophicTest(truthThreshold = 0.65, indeterminacyThreshold = 0.3)
    void testLenientFuzzyMatching(ExtensionContext context) {
        String userInput = "New Yrok";

        NeutrosophicAssertions.assertThat(
                userInput,
                FuzzyStringEvaluator.comparedTo("New York"),
                context
        );
    }

    @Test
    @NeutrosophicTest(truthThreshold = 0.95)
    void testStrictFuzzyMatching(ExtensionContext context) {
        String preciseInput = "New York";

        NeutrosophicAssertions.assertThat(
                preciseInput,
                FuzzyStringEvaluator.comparedTo("New York"),
                context
        );
    }

    @Test
    void testTraditionalApproach() {
        // Regular tests can still use the original API
        io.github.osmanys_perez.neutrosophy.NeutrosophicAssert.assertThat(
                "hello",
                FuzzyStringEvaluator.comparedTo("hello"),
                io.github.osmanys_perez.neutrosophy.NeutrosophicContext.defaultContext()
        ).isTrue();
    }

}