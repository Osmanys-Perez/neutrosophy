package io.github.osmanys_perez.neutrosophy.junit5;

import io.github.osmanys_perez.neutrosophy.Evaluator;
import io.github.osmanys_perez.neutrosophy.NeutrosophicAssert;
import io.github.osmanys_perez.neutrosophy.NeutrosophicAssertion;
import io.github.osmanys_perez.neutrosophy.NeutrosophicContext;
import org.junit.jupiter.api.extension.ExtensionContext;

public final class NeutrosophicAssertions {

    private NeutrosophicAssertions() {
        // Utility class
    }

    /**
     * The ONLY assertThat method. Returns the NeutrosophicAssertion for fluent chaining.
     * It is the caller's responsibility to call .isTrue(), .isFalse(), .isAccepted(), or .isRejected().
     */
    public static <T> NeutrosophicAssertion<T> assertThat(T actual, Evaluator<T> evaluator, ExtensionContext context) {
        NeutrosophicContext neutrosophicContext = NeutrosophicTestExtension.getContext(context);
        if (neutrosophicContext == null) {
            throw new IllegalStateException("No NeutrosophicContext found. Please add @NeutrosophicTest annotation.");
        }
        return NeutrosophicAssert.assertThat(actual, evaluator, neutrosophicContext);
    }
}