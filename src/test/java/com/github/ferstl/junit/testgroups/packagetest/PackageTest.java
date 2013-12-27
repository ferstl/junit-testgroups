package com.github.ferstl.junit.testgroups.packagetest;

import org.junit.ClassRule;
import org.junit.Test;

import com.github.ferstl.junit.testgroups.TestGroupRule;

public class PackageTest {

  public static final String TEST_GROUP_KEY = "packagetestgroup";
  public static final String TEST_GROUP_NAME = "package";

  @ClassRule
  public static TestGroupRule rule = new TestGroupRule();

  @Test
  public void test() {
    throw new IllegalStateException("boom");
  }
}
