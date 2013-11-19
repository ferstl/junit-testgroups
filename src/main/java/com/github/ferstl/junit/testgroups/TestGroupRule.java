package com.github.ferstl.junit.testgroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestGroupRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    TestGroup testGroup = description.getAnnotation(TestGroup.class);

    if (testGroup == null) {
      throw new IllegalStateException("Test " + description.getDisplayName() + " is not in a test group.");
    }

    String key = testGroup.key();
    Collection<String> enabledGroups = getEnabledTestGroups(key);
    Collection<String> declaredGroups = getDeclaredTestGroups(testGroup);

    if (isGroupEnabled(enabledGroups, declaredGroups)) {
      return base;
    }

    return new SkipStatement(enabledGroups, declaredGroups);
  }


  static Collection<String> getEnabledTestGroups(String key) {
    Collection<String> enabledGroups = split(System.getProperty(key, TestGroup.DEFAULT_GROUP));

    return enabledGroups;
  }


  static Collection<String> getDeclaredTestGroups(TestGroup testGroup) {
    String[] declaredGroups = testGroup.value();
    if (declaredGroups.length != 0) {
      return new HashSet<>(Arrays.asList(declaredGroups));
    }

    return Collections.singletonList(TestGroup.DEFAULT_GROUP);
  }


  static boolean isGroupEnabled(Collection<String> enabledGroups, Collection<String> declaredGroups) {
    if (enabledGroups.contains(TestGroup.ALL_GROUPS)) {
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
    if (commaSeparated == null || commaSeparated.isEmpty()) {
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
