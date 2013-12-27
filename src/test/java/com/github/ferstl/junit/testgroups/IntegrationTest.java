package com.github.ferstl.junit.testgroups;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static org.junit.Assert.assertEquals;

/**
 * A bunch of tests that use {@link JUnitCore#runClasses(Class...)}.
 */
public class IntegrationTest {

  private static final String USER_DEFINED_KEY = "myKey";
  private static final String USER_DEFINED_GROUP = "myGroup";

  @After
  public void after() {
    System.clearProperty(TestGroup.DEFAULT_KEY);
    System.clearProperty(USER_DEFINED_KEY);
  }

  @Test
  public void userDefinedGroupDisabled() {
    Result result = JUnitCore.runClasses(UserDefinedGroup.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getFailureCount());
  }

  @Test
  public void userDefinedGroupEnabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(UserDefinedGroup.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(2, result.getFailureCount());
  }

  @Test
  public void testGroupInheritenceDisabled() {
    Result result = JUnitCore.runClasses(SubClass.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getFailureCount());
  }

  @Test
  public void testGroupInheritenceEnabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(SubClass.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getFailureCount());
  }

  @Test
  public void userDefinedKeyDisabled() {
    Result result = JUnitCore.runClasses(UserDefinedKey.class);
    assertEquals(0, result.getFailureCount());
    assertEquals(1, result.getIgnoreCount());
  }

  @TestGroup(USER_DEFINED_GROUP)
  public static class UserDefinedGroup {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {
      throw new IllegalStateException("boom");
    }

    @Test
    public void test2() {
      throw new IllegalStateException("boom");
    }

  }


  /**
   * Abstract class defining a test group and the {@link TestGroupRule}.
   */
  @TestGroup(USER_DEFINED_GROUP)
  public static abstract class BaseClass {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();
  }

  /**
   * Subclass of {@link BaseClass} containing the actual tests.
   */
  public static class SubClass extends BaseClass {

    @Test
    public void test() {
      throw new IllegalStateException("boom");
    }
  }

  /**
   * This test class' test group has a user-defined key.
   */
  @TestGroup(key = USER_DEFINED_KEY, value = USER_DEFINED_GROUP)
  public static class UserDefinedKey {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {
      throw new IllegalStateException("boom");
    }
  }
}
