# JUnit Test Groups
*- Divides your JUnit tests into groups which can be executed separately or all at once*

[![Build Status](https://travis-ci.org/ferstl/junit-testgroups.svg?branch=master)](https://travis-ci.org/ferstl/junit-testgroups) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/junit-testgroups/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ferstl/junit-testgroups)

## Background
In a recent project we wanted to write each test as JUnit test no matter if the tests are real unit tests or longer running integration tests. The reason behind this idea is that everyone should be able to run all tests with Maven or directly within the IDE with as less effort as possible. Moreover, we wanted to prevent our CI build and also local Maven builds from executing the integration tests all the time (other CI jobs are executing them). So to make a long story short, we needed a mechanism to group our JUnit tests and to execute these groups of tests independently from each other.

There are already several tools that allow test grouping:
- JUnit's `Categories` runner
- Spring's `SpringJunit4ClassRunner` and its `@IfProfileValue` mechanism
- Maven's [Failsafe](http://maven.apache.org/surefire/maven-failsafe-plugin/) plugin
- ...

However, all of these tools have their disadvantages. We are not only using regular JUnit tests but also parameterized tests and Theories. Since these tests require different test runners, JUnit's `Categories` runner is not an option. Spring's `@IfProfileValue` mechanism is also not an option for the same reason and the maven-failsafe-plugin does not integrate well with IDEs. 

This project tries to achieve the same goals by using JUnit's class rule mechanism. It works pretty well but there are some pitfalls as well (see below).


## How to use

### Dependencies:

    <dependency>
      <groupId>com.github.ferstl</groupId>
      <artifactId>junit-testgroups</artifactId>
      <version>1.0.0</version>
      <scope>test</scope>
    </dependency>
    
    <!-- JUnit will not come transitively with junit-testgroups! -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>


### Grouping tests

First, annotate your tests with `@TestGroup` and declare the group to which the test belongs. In order to make the test groups work, you also need to define an instance of `TestGroupRule` as a class rule for each of your tests.

    @TestGroup("integration")
    public class MyIntegrationTest {
       @ClassRule
       public static TestGroupRule rule = new TestGroupRule();
       
       ...
    }

A test may also belong to several groups:

    @TestGroup("group1", "group2")
    public class MyTest {
       @ClassRule
       public static TestGroupRule rule = new TestGroupRule();
       
       ...
    }
    
Tests which don't declare a test group name are in an implicit default group. All tests belonging to the default group will run if no test group is defined (see "Running the Tests" below).
    
    @TestGroup
    public class MyRegularUnitTest {
      @ClassRule
      public static TestGroupRule rule = new TestGroupRule();
    }

Instead of defining the same test groups and the class rules over and over again, a more practical approach is defining abstract test classes for all your supported test groups:

    @TestGroup("integration")
    public abstract class AbstractIntegrationTest {
      @ClassRule
      public static TestGroupRule rule = new TestGroupRule();
      
      ...
    }
    
    public class MyIntegrationTest extends AbstractIntegrationTest {
      @Test
      public void testStuff() { ... }
    }


### Running the tests
Once your tests are grouped, you can run them by simply defining your test groups to be executed in a system property called `testgroup`:
- Execute a simple test group: `-Dtestgroup=integration`
- Execute multiple test groups: `-Dtestgroup=group1,group2`
- Execute all test groups: `-Dtestgroup=all`

When no `testgroup` system property is defined, all tests without an explicitly declared test group will be executed.
    

### More advanced Stuff

Test groups can also be defined on package level. However, the class rule still needs to be defined in your test classes.

    // package-info.java
    @TestGroup("integration")
    package my.project.integrationtests

In case the system property key `testgroup` does not work for you, you can define another key:

    @TestGroup(key = "mykey", value = "integration")
    public class MyIntegrationTest {
      @ClassRule
      public static TestGroupRule rule = new TestGroupRule();
      
      ...
    }
    
Tests can then be executed with the system property `-Dmykey=integration`.

## The good Things
- Works with all test runners extending `ParentRunner`, which includes all test runners of the JUnit library (`BlockJUnit4ClassRunner`, `Parameterized`, `Theories`, `SpringJunit4ClassRunner`, etc.).
- Works with Maven **and** in your IDE
- All tests in the (implicit) default group will run without defining anything
- It's possible to run all tests no matter in what group they are

## The bad Things
- **All** tests require a `@TestGroup` annotation **and** an instance of `TestGroupRule` as `@ClassRule`. All other tests will be executed any time.
- There is no defined execution order of test rules. So if a test is in a test group and uses other test rules, these test rules might get executed even if the test is not supposed to run. You should use `RuleChain`s (with `TestGroupRule` as first rule) in case you are using other test rules.
- In case your test runner is a subclass of `ParentRunner`, the `#getChildren()` method of your test runner will **always** be executed, no matter if the test class is supposed to be executed.
