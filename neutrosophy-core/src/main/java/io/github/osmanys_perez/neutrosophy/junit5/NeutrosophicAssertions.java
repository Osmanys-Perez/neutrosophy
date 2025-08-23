package io.github.osmanys_perez.neutrosophy.junit5;

import io.github.osmanys_perez.neutrosophy.Evaluator;
import io.github.osmanys_perez.neutrosophy.NeutrosophicAssert;
import io.github.osmanys_perez.neutrosophy.NeutrosophicContext;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit-specific assertions for neutrosophic testing.
 */
public final class NeutrosophicAssertions {

    private NeutrosophicAssertions() {
        // Utility class
    }

    /**
     * Asserts on a value using the NeutrosophicContext from @NeutrosophicTest annotation.
     */
    public static <T> void assertThat(T actual, Evaluator<T> evaluator, ExtensionContext context) {
        NeutrosophicContext neutrosophicContext = NeutrosophicTestExtension.getContext(context);
        if (neutrosophicContext == null) {
            throw new IllegalStateException("No NeutrosophicContext found. Please add @NeutrosophicTest annotation.");
        }
        NeutrosophicAssert.assertThat(actual, evaluator, neutrosophicContext).isTrue();
    }
}