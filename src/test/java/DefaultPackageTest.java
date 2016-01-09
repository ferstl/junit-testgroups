import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import com.github.ferstl.junit.testgroups.TestGroup;
import com.github.ferstl.junit.testgroups.TestGroupRule;
import static org.junit.Assert.assertEquals;

/**
 * Tests for the default package. Classes from the default package may not be used in other packages.
 * See {@link http://docs.oracle.com/javase/specs/jls/se7/html/jls-7.html#jls-7.5}.
 */
public class DefaultPackageTest {

  @Test
  public void classRuleWithoutTestGroupTest() {
    Result result = JUnitCore.runClasses(ClassRuleWithoutTestGroup.class);

    assertEquals(1, result.getRunCount());
    assertEquals(0, result.getFailureCount());
  }

  /**
   * This class defines the {@link TestGroupRule} but has no {@link TestGroup} annotation.
   */
  public static class ClassRuleWithoutTestGroup {
    @ClassRule
    public static TestGroupRule rule = new TestGroupRule();

    @Test
    public void test() {}
  }
}
