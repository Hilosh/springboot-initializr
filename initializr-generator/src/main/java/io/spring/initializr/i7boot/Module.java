package io.spring.initializr.i7boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jyuh
 * @date 2021-10-21 10:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {
    String suffix() default "";

    String dependencyGroupId() default "*";

    String dependencyArtifactId() default "*";

    String version() default "*";
}
