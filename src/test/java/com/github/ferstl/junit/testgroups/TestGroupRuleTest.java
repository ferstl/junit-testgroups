package com.github.ferstl.junit.testgroups;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.github.ferstl.junit.testgroups.TestGroupRule.SkipStatement;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link TestGroupRule}.
 */
public class TestGroupRuleTest {

  @Rule
  public ExpectedException expEx = ExpectedException.none();

  private TestGroupRule rule;
  private Statement statement;
  private TestGroup testGroup;
  private Description description;

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Before
  public void before() {
    this.rule = new TestGroupRule();
    this.statement = mock(Statement.class);
    this.testGroup = mock(TestGroup.class);
    when(this.testGroup.key()).thenReturn(TestGroup.DEFAULT_KEY);
    when(this.testGroup.value()).thenReturn(new String[]{});
    this.description = mock(Description.class);
    when(this.description.getDisplayName()).thenReturn("displayName");
    when(this.description.getAnnotation(TestGroup.class)).thenReturn(this.testGroup);
    when(this.description.getTestClass()).thenReturn((Class) getClass());
  }

  @After
  public void after() {
    System.clearProperty(TestGroup.DEFAULT_KEY);
  }

  @Test
  public void declaredDefaultGroup() {
    assertEquals(this.statement, this.rule.apply(this.statement, this.description));
  }

  @Test
  public void implicitDefaultGroup() {
    when(this.testGroup.value()).thenReturn(new String[0]);

    assertEquals(this.statement, this.rule.apply(this.statement, this.description));
  }

  @Test
  public void allGroups() {
    when(this.testGroup.value()).thenReturn(new String[] {"group1,group2"});
    System.setProperty(TestGroup.DEFAULT_KEY, TestGroup.ALL_GROUPS);

    assertEquals(this.statement, this.rule.apply(this.statement, this.description));
  }

  @Test
  public void noAnnotation() {
    when(this.description.getAnnotation(TestGroup.class)).thenReturn(null);

    assertEquals(this.statement, this.rule.apply(this.statement, this.description));
  }

  @Test
  public void singleMatchingGroup() {
    when(this.testGroup.value()).thenReturn(new String[]{"customGroup"});
    System.setProperty(TestGroup.DEFAULT_KEY, "customGroup");

    assertEquals(this.statement, this.rule.apply(this.statement, this.description));
  }

  @Test
  public void nonMatchingGroup() {
    System.setProperty(TestGroup.DEFAULT_KEY, "customGroup");

    assertThat(this.rule.apply(this.statement, this.description), instanceOf(SkipStatement.class));
  }

  @Test
  public void groupEnablementAllGroups() {
    List<String> declaredGroups = Arrays.asList("declaredGroup1", "declaredGroup2");
    List<String> enabledGroups = Collections.singletonList(TestGroup.ALL_GROUPS);

    assertTrue(TestGroupRule.isGroupEnabled(enabledGroups, declaredGroups));
  }

  @Test
  public void groupEnablementDefault() {
    List<String> declaredGroups = Collections.emptyList();
    List<String> enabledGroups = Collections.emptyList();

    assertTrue(TestGroupRule.isGroupEnabled(enabledGroups, declaredGroups));
  }

  @Test
  public void groupEnablementDeclaredAndEnabled() {
    List<String> declaredGroups = Arrays.asList("group1", "group2");
    List<String> enabledGroups = Arrays.asList("group2", "group3");

    assertTrue(TestGroupRule.isGroupEnabled(enabledGroups, declaredGroups));
  }

  @Test
  public void groupEnablementDeclaredAndNotEnabled() {
    List<String> declaredGroups = Arrays.asList("group1", "group2");
    List<String> enabledGroups = Arrays.asList("group3", "group4");

    assertFalse(TestGroupRule.isGroupEnabled(enabledGroups, declaredGroups));
  }

  @Test
  public void skipStatement() throws Throwable {
    SkipStatement skipStatement = new SkipStatement(Arrays.asList("foo", "bar"), Arrays.asList("baz", "blub"));

    this.expEx.expect(AssumptionViolatedException.class);
    this.expEx.expectMessage(containsString("foo"));
    this.expEx.expectMessage(containsString("bar"));
    this.expEx.expectMessage(containsString("baz"));
    this.expEx.expectMessage(containsString("blub"));

    skipStatement.evaluate();
  }

  @Test
  public void splitMultiple() {
    Collection<String> values = TestGroupRule.split("a,b,c");

    assertThat(values, containsInAnyOrder("a", "b", "c"));
  }

  @Test
  public void splitWithEmptyGroups() {
    Collection<String> values = TestGroupRule.split("a,,b,c");

    assertThat(values, containsInAnyOrder("a", "b", "c"));
  }

  @Test
  public void splitSingle() {
    Collection<String> values = TestGroupRule.split("a");

    assertThat(values, contains("a"));
  }

  @Test
  public void splitEmpty() {
    Collection<String> values = TestGroupRule.split("");

    assertThat(values, empty());
  }

  @Test
  public void splitNull() {
    Collection<String> values = TestGroupRule.split(null);

    assertThat(values, empty());
  }

}
