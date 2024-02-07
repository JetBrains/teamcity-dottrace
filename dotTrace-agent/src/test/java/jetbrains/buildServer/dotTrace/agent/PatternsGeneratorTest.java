

package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class PatternsGeneratorTest {
  private Mockery myCtx;
  private TextParser<Metrics> myReportPatternsParser;
  private RunnerParametersService myRunnerParametersService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    //noinspection unchecked
    myReportPatternsParser = (TextParser<Metrics>)myCtx.mock(TextParser.class);
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
  }

  @Test
  public void shouldGenerateContent() {
    // Given
    String expectedContent = "<Patterns>" +
                             "<Pattern>Method1</Pattern>" +
                             "<Pattern>Method2</Pattern>" +
                             "</Patterns>";

    final CommandLineSetup setup = new CommandLineSetup("tool", Collections.<CommandLineArgument>emptyList(), Collections.<CommandLineResource>emptyList());
    myCtx.checking(new Expectations() {{
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.THRESHOLDS_VAR);
      will(returnValue("thresholds"));

      oneOf(myReportPatternsParser).parse("thresholds");
      will(returnValue(new Metrics(Arrays.asList(new Metric("Method1", "100", "200"), new Metric("Method2", "200", "300")))));
    }});

    final PatternsGenerator instance = createInstance();

    // When
    final String content = instance.create(new Context(setup, new File("a"), new File("b"), new File("c"), new File("s")));

    // Then
    myCtx.assertIsSatisfied();
    then(content.trim().replace("\n", "").replace("\r", "").replace(" ", "")).isEqualTo(expectedContent.trim().replace("\n", "").replace("\r", "").replace(" ", ""));
  }

  @NotNull
  private PatternsGenerator createInstance()
  {
    return new PatternsGenerator(
      myReportPatternsParser,
      myRunnerParametersService,
      new XmlDocumentManagerImpl());
  }
}