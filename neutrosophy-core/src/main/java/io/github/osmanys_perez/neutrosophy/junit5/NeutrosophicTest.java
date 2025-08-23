package io.github.osmanys_perez.neutrosophy.junit5;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method as using neutrosophic assertions.
 * Allows configuration of default context parameters for all assertions within the test.
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(NeutrosophicTestExtension.class)
public @interface NeutrosophicTest {

    double truthThreshold() default 0.8;
    double indeterminacyThreshold() default 0.2;
    double falsityThreshold() default 0.2;
    double tolerance() default 0.01;
}