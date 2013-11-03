package com.github.ferstl.junit.testgroups;

import java.util.Collection;

import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

/**
 * JUnit tests for {@link TestGroupRule}.
 */
public class TestGroupRuleTest {

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
