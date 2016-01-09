import org.junit.ClassRule;
import org.junit.Test;
import com.github.ferstl.junit.testgroups.TestGroupRule;

public class DefaultPackageTest {

  @ClassRule
  public static TestGroupRule rule = new TestGroupRule();

  @Test
  public void test() {}
}
