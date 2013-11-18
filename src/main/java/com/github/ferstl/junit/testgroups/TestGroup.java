package com.github.ferstl.junit.testgroups;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface TestGroup {
  static final String DEFAULT_KEY = "testgroup";
  static final String DEFAULT_GROUP = "defaultGroup";

  String key() default DEFAULT_KEY;
  String[] value() default DEFAULT_GROUP;
}
