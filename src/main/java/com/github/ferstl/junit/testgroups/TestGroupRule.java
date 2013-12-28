package com.github.ferstl.junit.testgroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * JUnit rule that evaluates {@link TestGroup} annotations on tests and will skip all tests that do not belong to the
 * currently enabled group. This rule is supposed to be used as JUnit class rule!
 */
public class TestGroupRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    TestGroup testGroup = findTestGroup(description);

    if (testGroup == null) {
      return base;
    }

    String key = testGroup.key();
    Collection<String> enabledGroups = getEnabledTestGroups(key);
    Collection<String> declaredGroups = getDeclaredTestGroups(testGroup);

    if (isGroupEnabled(enabledGroups, declaredGroups)) {
      return base;
    }

    return new SkipStatement(enabledGroups, declaredGroups);
  }


  TestGroup findTestGroup(Description description) {
    TestGroup testGroup = description.getAnnotation(TestGroup.class);

    // Try the package it the class is not annotated.
    if (testGroup == null) {
      testGroup = description.getTestClass().getPackage().getAnnotation(TestGroup.class);
    }

    return testGroup;
  }


  static Collection<String> getEnabledTestGroups(String key) {
    Collection<String> enabledGroups = split(System.getProperty(key));

    return enabledGroups;
  }


  static Collection<String> getDeclaredTestGroups(TestGroup testGroup) {
    String[] declaredGroups = testGroup.value();
    if (declaredGroups.length != 0) {
      return new HashSet<>(Arrays.asList(declaredGroups));
    }

    return Collections.emptyList();
  }


  /**
   * A test group is enabled if:
   * <ul>
   * <li> {@link TestGroup#ALL_GROUPS} is defined</li>
   * <li> The declared test groups contain at least one defined test group</li>
   * <li> No test group is declared and no test group is defined.</li>
   * </ul>
   * @param enabledGroups Test groups that are enabled for execution.
   * @param declaredGroups Test groups that are declared in the {@link TestGroup} annotation.
   * @return
   */
  static boolean isGroupEnabled(Collection<String> enabledGroups, Collection<String> declaredGroups) {
    if (enabledGroups.contains(TestGroup.ALL_GROUPS)
        || (enabledGroups.isEmpty() && declaredGroups.isEmpty())) {
      return true;
    }

    for (String enabledGroup : enabledGroups) {
      if (declaredGroups.contains(enabledGroup)) {
        return true;
      }
    }

    return false;
  }


  static Collection<String> split(String commaSeparated) {
    if (commaSeparated == null || commaSeparated.trim().isEmpty()) {
      return Collections.emptySet();
    }

    String[] splits = commaSeparated.split(",+");

    return new HashSet<>(Arrays.asList(splits));
  }

  static class SkipStatement extends Statement {

    Collection<String> enabledGroups;
    Collection<String> testGroups;

    public SkipStatement(Collection<String> enabledGroups, Collection<String> testGroups) {
      this.enabledGroups = enabledGroups;
      this.testGroups = testGroups;
    }

    @Override
    public void evaluate() throws Throwable {

      throw new AssumptionViolatedException(
          "None of the test groups " + this.testGroups + " are enabled. Enabled test groups: " + this.enabledGroups);
    }
  }
}
