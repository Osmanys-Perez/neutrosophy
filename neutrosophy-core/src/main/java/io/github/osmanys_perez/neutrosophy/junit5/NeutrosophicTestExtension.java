package io.github.osmanys_perez.neutrosophy.junit5;

import io.github.osmanys_perez.neutrosophy.NeutrosophicContext;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Method;

/**
 * JUnit 5 extension that provides neutrosophic testing capabilities.
 */
public class NeutrosophicTestExtension implements BeforeEachCallback, ParameterResolver { // <-- Add ParameterResolver

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        NeutrosophicTest annotation = testMethod.getAnnotation(NeutrosophicTest.class);

        if (annotation != null) {
            NeutrosophicContext neutrosophicContext = NeutrosophicContext.builder()
                    .withTruthThreshold(annotation.truthThreshold())
                    .withIndeterminacyThreshold(annotation.indeterminacyThreshold())
                    .withFalsityThreshold(annotation.falsityThreshold())
                    .withTolerance(annotation.tolerance())
                    .build();

            context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL)
                    .put("neutrosophicContext", neutrosophicContext);
        }
    }

    // === NEW METHODS FOR PARAMETER RESOLUTION ===
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // We can resolve ExtensionContext parameters
        return parameterContext.getParameter().getType().equals(ExtensionContext.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Simply return the ExtensionContext that JUnit provides to us
        return extensionContext;
    }

    public static NeutrosophicContext getContext(ExtensionContext context) {
        return context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL)
                .get("neutrosophicContext", NeutrosophicContext.class);
    }
}