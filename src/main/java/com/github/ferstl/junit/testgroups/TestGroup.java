package com.github.ferstl.junit.testgroups;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assigns a JUnit test to a named group. Together with the class rule {@link TestGroupRule} the grouped tests will only
 * be executed when they are enabled by a system property.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface TestGroup {
  static final String DEFAULT_KEY = "testgroup";
  static final String ALL_GROUPS = "all";

  /** Name of the system property that enables the test groups. The default is {@value #DEFAULT_KEY}. */
  String key() default DEFAULT_KEY;

  /**
   * Name of the test group(s) to which a test belongs. If no name is defined, the test will belong to an implicit
   * default group.
   */
  String[] value() default {};
}
