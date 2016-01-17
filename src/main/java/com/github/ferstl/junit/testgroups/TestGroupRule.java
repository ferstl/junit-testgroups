/*
 * Copyright (c) 2013 Stefan Ferstl <st.ferstl@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.ferstl.junit.testgroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Assume;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * JUnit rule that evaluates {@link TestGroup} annotations on tests and will skip all tests that do not belong to the
 * currently enabled group. This rule is supposed to be used as JUnit class rule!
 */
public class TestGroupRule implements TestRule {

  /**
   * @deprecated Use one of the static factory methods {@link #create()} or {@link #chain()}.
   */
  @Deprecated
  public TestGroupRule() {}

  /**
   * Creates an instance of {@link TestGroupRule}.
   */
  public static TestGroupRule create() {
    return new TestGroupRule();
  }

  /**
   * Creates a {@link RuleChain} with the {@link TestGroupRule} as outer rule. This is equivalent to:
   * <pre>
   * RuleChain.outerRule(TestGroupRule.create());
   * </pre>
   */
  public static RuleChain chain() {
    return RuleChain.outerRule(create());
  }

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


  static TestGroup findTestGroup(Description description) {
    TestGroup testGroup = description.getAnnotation(TestGroup.class);

    // Try the package it the class is not annotated.
    if (testGroup == null) {
      Package pkg = description.getTestClass().getPackage();
      if (pkg != null) {
        testGroup = pkg.getAnnotation(TestGroup.class);
      }
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
    if (enabledGroups.contains(TestGroup.ALL_GROUPS) || (enabledGroups.isEmpty() && declaredGroups.isEmpty())) {
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
      Assume.assumeTrue("None of the test groups " + this.testGroups + " are enabled. Enabled test groups: " + this.enabledGroups, false);
    }
  }
}
