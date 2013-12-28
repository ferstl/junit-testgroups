package com.github.ferstl.junit.testgroups;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import com.github.ferstl.junit.testgroups.packagetest.PackageTest;
import com.github.ferstl.junit.testgroups.packagetest.subpackage.SubPackageTest;

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
    System.clearProperty(PackageTest.TEST_GROUP_KEY);
  }

  @Test
  public void defaultTestGroupEnabled() {
    // No system property has to be set to enable the default test group.
    Result result = JUnitCore.runClasses(DefaultTestGroup.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getRunCount());
  }

  @Test
  public void defaultTestGroupDisabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(DefaultTestGroup.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void defaultTestGroupWithoutAnnotationEnabled() {
    // No system property has to be set to enable the default test group.
    Result result = JUnitCore.runClasses(DefaultTestGroup.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getRunCount());
  }

  @Test
  public void defaultTestGroupWithoutAnnotationDisabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(DefaultTestGroup.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void userDefinedGroupDisabled() {
    Result result = JUnitCore.runClasses(UserDefinedGroup.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void userDefinedGroupEnabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(UserDefinedGroup.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(2, result.getRunCount());
  }

  @Test
  public void testGroupInheritenceDisabled() {
    Result result = JUnitCore.runClasses(SubClass.class);
    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void testGroupInheritenceEnabled() {
    System.setProperty(TestGroup.DEFAULT_KEY, USER_DEFINED_GROUP);

    Result result = JUnitCore.runClasses(SubClass.class);
    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getRunCount());
  }

  @Test
  public void userDefinedKeyDisabled() {
    Result result = JUnitCore.runClasses(UserDefinedKey.class);
    assertEquals(0, result.getRunCount());
    assertEquals(1, result.getIgnoreCount());
  }

  @Test
  public void packageTestDisabled() {
    Result result = JUnitCore.runClasses(PackageTest.class);

    assertEquals(1, result.getIgnoreCount());
    assertEquals(0, result.getRunCount());
  }

  @Test
  public void packageTestEnabled() {
    System.setProperty(PackageTest.TEST_GROUP_KEY, PackageTest.TEST_GROUP_NAME);
    Result result = JUnitCore.runClasses(PackageTest.class);

    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getRunCount());
  }

  @Test
  public void subPackageTest() {
    // Sub packages are not affected of a test group annotation in the parent package.
    Result result = JUnitCore.runClasses(SubPackageTest.class);

    assertEquals(0, result.getIgnoreCount());
    assertEquals(1, result.getRunCount());
  }

  /**
   * Test class with the default test group.
   */
  @TestGroup
  public static class DefaultTestGroup {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {}
  }

  public static class DefaultTestGroupWithoutAnnotation {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();
  }

  @TestGroup(USER_DEFINED_GROUP)
  public static class UserDefinedGroup {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {}

    @Test
    public void test2() {}

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
    public void test() {}
  }

  /**
   * This test class' test group has a user-defined key.
   */
  @TestGroup(key = USER_DEFINED_KEY, value = USER_DEFINED_GROUP)
  public static class UserDefinedKey {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {}
  }
}
