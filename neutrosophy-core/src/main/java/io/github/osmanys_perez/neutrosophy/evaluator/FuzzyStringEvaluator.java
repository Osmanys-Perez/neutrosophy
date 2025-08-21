package io.github.osmanys_perez.neutrosophy.evaluator;

import io.github.osmanys_perez.neutrosophy.Evaluator;
import io.github.osmanys_perez.neutrosophy.NeutrosophicValue;

/**
 * Evaluates strings based on fuzzy similarity to an expected value.
 */
public final class FuzzyStringEvaluator implements Evaluator<String> {

    private final String expected;
    // A threshold for what similarity score (0.0 to 1.0) is considered a definite match.
    // This influences how we map similarity to the (T, I, F) triplet.
    private final double perfectMatchThreshold;

    /**
     * Private constructor. Use {@link #comparedTo(String)} to create an instance.
     */
    private FuzzyStringEvaluator(String expected, double perfectMatchThreshold) {
        this.expected = expected;
        this.perfectMatchThreshold = perfectMatchThreshold;
    }

    /**
     * Creates a FuzzyStringEvaluator builder with the expected string to compare against.
     * @param expected the expected string
     * @return an instance of the evaluator's builder
     */
    public static FuzzyStringEvaluator comparedTo(String expected) {
        return new FuzzyStringEvaluator(expected, 0.85); // Default threshold
    }

    /**
     * Fluently sets a custom perfect match threshold.
     * @param threshold a value between 0.0 and 1.0. 0.85 means a similarity score of 0.85 is considered a perfect match (T=1.0).
     * @return this evaluator instance for method chaining
     */
    public FuzzyStringEvaluator withPerfectMatchThreshold(double threshold) {
        return new FuzzyStringEvaluator(this.expected, threshold);
    }

    @Override
    public NeutrosophicValue evaluate(String actual) {
        if (expected == null && actual == null) {
            return NeutrosophicValue.TRUE; // Both are null, perfect match.
        }
        if (expected == null || actual == null) {
            // One is null, the other is not. This is likely a definite mismatch.
            // But we could add a little indeterminacy, e.g., maybe it's an empty string vs null?
            return new NeutrosophicValue(0.0, 0.1, 0.9); // Mostly false
        }

        double similarity = calculateSimilarity(expected, actual);

        // **Neutrosophic Interpretation of Similarity**:
        // This is the key logic. We map a continuous similarity score to the (T, I, F) components.

        if (similarity >= perfectMatchThreshold) {
            // High similarity: Strong truth, very low indeterminacy and falsehood.
            // Scale truth towards 1.0 as we approach the threshold.
            double truth = 0.7 + (0.3 * ((similarity - perfectMatchThreshold) / (1.0 - perfectMatchThreshold)));
            return new NeutrosophicValue(Math.min(truth, 1.0), 0.05, 0.05);
        } else if (similarity >= perfectMatchThreshold / 2) {
            // Medium similarity: This is the zone of maximum indeterminacy.
            // It's not clearly right or wrong. We split the membership.
            double baseTruth = similarity / perfectMatchThreshold; // 0.0 to 1.0
            double baseFalsity = 1.0 - baseTruth;
            // Introduce significant indeterminacy
            return new NeutrosophicValue(baseTruth * 0.6, 0.3, baseFalsity * 0.6);
        } else {
            // Low similarity: Strong falsehood, very low truth and indeterminacy.
            double falsity = 0.7 + (0.3 * (1.0 - (similarity / (perfectMatchThreshold / 2))));
            return new NeutrosophicValue(0.05, 0.05, Math.min(falsity, 1.0));
        }
    }

    /**
     * Calculates a normalized similarity score between 0.0 (completely different) and 1.0 (identical).
     * Uses Levenshtein distance.
     * @param s1 the first string
     * @param s2 the second string
     * @return the similarity score
     */
    private double calculateSimilarity(String s1, String s2) {
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) {
            return 1.0; // Both strings are empty
        }
        int distance = calculateLevenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLength);
    }

    private int calculateLevenshteinDistance(CharSequence s1, CharSequence s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int a, int b, int c) {
        return Math.min(a, Math.min(b, c));
    }
}