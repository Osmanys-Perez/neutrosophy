package io.github.osmanys_perez.neutrosophy.evaluator;

import io.github.osmanys_perez.neutrosophy.Evaluator;
import io.github.osmanys_perez.neutrosophy.NeutrosophicValue;

import java.util.function.Supplier;


/**
 * Evaluates whether executable code throws an expected exception.
 * Handles cases where exception types might be related or messages might be approximate.
 */
public final class ExceptionThrownEvaluator implements Evaluator<Supplier<Object>> {

    private final Class<? extends Throwable> expectedExceptionType;
    private final String expectedMessagePattern;

    private ExceptionThrownEvaluator(Class<? extends Throwable> expectedExceptionType, String expectedMessagePattern) {
        this.expectedExceptionType = expectedExceptionType;
        this.expectedMessagePattern = expectedMessagePattern;
    }

    public static ExceptionThrownEvaluator throwsException(Class<? extends Throwable> expectedExceptionType) {
        return new ExceptionThrownEvaluator(expectedExceptionType, null);
    }

    public ExceptionThrownEvaluator withMessageContaining(String messagePattern) {
        return new ExceptionThrownEvaluator(this.expectedExceptionType, messagePattern);
    }

    @Override
    public NeutrosophicValue evaluate(Supplier<Object> codeSupplier) {
        try {
            codeSupplier.get();
            // If we get here, no exception was thrown
            return new NeutrosophicValue(0.0, 0.1, 0.9); // Mostly false
        } catch (Throwable actualException) {
            // Check exception type match
            double typeSimilarity = calculateExceptionTypeSimilarity(actualException.getClass(), expectedExceptionType);

            // Check message match if pattern was provided
            double messageSimilarity = 1.0;
            if (expectedMessagePattern != null && actualException.getMessage() != null) {
                messageSimilarity = calculateMessageSimilarity(actualException.getMessage(), expectedMessagePattern);
            }

            double truth = typeSimilarity * messageSimilarity;
            double indeterminacy = 0.1 * (1 - truth); // Some uncertainty about exception handling
            double falsity = 1 - truth - indeterminacy;

            return new NeutrosophicValue(truth, indeterminacy, falsity);
        }
    }

    private double calculateExceptionTypeSimilarity(Class<?> actual, Class<?> expected) {
        if (actual.equals(expected)) {
            return 1.0; // Exact match
        }
        if (expected.isAssignableFrom(actual)) {
            return 0.8; // Subclass match
        }
        // Check if they share a common hierarchy
        if (haveCommonAncestor(actual, expected)) {
            return 0.4; // Related types
        }
        return 0.0; // Unrelated types
    }

    private boolean haveCommonAncestor(Class<?> c1, Class<?> c2) {
        // Simplified check for common exception ancestry
        return Throwable.class.isAssignableFrom(c1) && Throwable.class.isAssignableFrom(c2);
    }

    private double calculateMessageSimilarity(String actualMessage, String expectedPattern) {
        // Convert both to lowercase for case-insensitive matching
        String actualLower = actualMessage.toLowerCase();
        String expectedLower = expectedPattern.toLowerCase();
        // Use fuzzy string matching for message similarity
        FuzzyStringEvaluator fuzzyMatcher = FuzzyStringEvaluator.comparedTo(expectedLower)
                .withPerfectMatchThreshold(0.7);
        return fuzzyMatcher.evaluate(actualLower).truth();
    }
}