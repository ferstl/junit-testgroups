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

    String key = testGroup.key();
    Collection<String> testGroups = new HashSet<>(Arrays.asList(testGroup.value()));

    Collection<String> enabledGroups = split(System.getProperty(key));
    if (enabledGroups.isEmpty() || isGroupDefined(enabledGroups, testGroups)) {
      return base;
    }

    return new SkipStatement(enabledGroups, testGroups);
  }


  static boolean isGroupDefined(Collection<String> definedGroups, Collection<String> testGroups) {
    for (String definedGroup : definedGroups) {
      if (testGroups.contains(definedGroup)) { return true; }
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
