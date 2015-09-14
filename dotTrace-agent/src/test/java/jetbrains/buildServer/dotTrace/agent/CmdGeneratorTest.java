package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class CmdGeneratorTest {
  private static final String ourlineSeparator = System.getProperty("line.separator");
  private Mockery myCtx;
  private RunnerParametersService myRunnerParametersService;
  private CommandLineArgumentsService myCommandLineArgumentsService;
  private FileService myFileService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    myCommandLineArgumentsService = myCtx.mock(CommandLineArgumentsService.class);
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
    myFileService = myCtx.mock(FileService.class);
  }

  @DataProvider(name = "parseGenerateContentCases")
  public Object[][] getParseGenerateContentCases() {
    return new Object[][] {
      {
        null,
        new File("path", "abc").getAbsoluteFile(),
        "ProfilerCmd" + ourlineSeparator
        + "SET EXIT_CODE=%ERRORLEVEL%" + ourlineSeparator
        + "ReporterCmd" + ourlineSeparator
        + "@echo EXIT_CODE=%EXIT_CODE%" + ourlineSeparator
        + "exit %EXIT_CODE%"
      },
      {
        new File("root"),
        new File("path", "abc"),
        "ProfilerCmd" + ourlineSeparator
        + "SET EXIT_CODE=%ERRORLEVEL%" + ourlineSeparator
        + "ReporterCmd" + ourlineSeparator
        + "@echo EXIT_CODE=%EXIT_CODE%" + ourlineSeparator
        + "exit %EXIT_CODE%"
      },
    };
  }

  @Test(dataProvider = "parseGenerateContentCases")
  public void shouldGenerateContent(@Nullable final File checkoutPath, @NotNull final File toolPath, @NotNull final String expectedContent) {
    // Given
    final File projectFile = new File("project");
    final File snapshotFile = new File("snapshot");
    final File patternsFile = new File("patterns");
    final File reportFile = new File("report");
    File path;
    if(checkoutPath != null) {
      path = new File(checkoutPath, toolPath.getPath());
    }
    else {
      path = toolPath;
    }

    final File consoleProfilerFile = new File(path, CmdGenerator.DOT_TRACE_EXE_NAME);
    final File reporterFile = new File(path, CmdGenerator.DOT_TRACE_REPORTER_EXE_NAME);

    final CommandLineSetup setup = new CommandLineSetup("tool", Collections.<CommandLineArgument>emptyList(), Collections.<CommandLineResource>emptyList());
    myCtx.checking(new Expectations() {{
      oneOf(myRunnerParametersService).getRunnerParameter(Constants.PATH_VAR);
      will(returnValue(toolPath.getPath()));

      allowing(myFileService).getCheckoutDirectory();
      will(returnValue(checkoutPath));

      oneOf(myFileService).validatePath(consoleProfilerFile);
      oneOf(myFileService).validatePath(reporterFile);

      oneOf(myCommandLineArgumentsService).createCommandLineString(Arrays.asList(
        new CommandLineArgument(consoleProfilerFile.getPath(), CommandLineArgument.Type.TOOL),
        new CommandLineArgument(projectFile.getPath(), CommandLineArgument.Type.PARAMETER),
        new CommandLineArgument(snapshotFile.getPath(), CommandLineArgument.Type.PARAMETER)));

      will(returnValue("ProfilerCmd"));

      oneOf(myCommandLineArgumentsService).createCommandLineString(Arrays.asList(
        new CommandLineArgument(reporterFile.getPath(), CommandLineArgument.Type.TOOL),
        new CommandLineArgument("/reporting", CommandLineArgument.Type.PARAMETER),
        new CommandLineArgument(snapshotFile.getPath(), CommandLineArgument.Type.PARAMETER),
        new CommandLineArgument(patternsFile.getPath(), CommandLineArgument.Type.PARAMETER),
        new CommandLineArgument(reportFile.getPath(), CommandLineArgument.Type.PARAMETER)));

      will(returnValue("ReporterCmd"));
    }});

    final CmdGenerator instance = createInstance();

    // When
    final String content = instance.create(new Context(setup, projectFile, snapshotFile, patternsFile, reportFile));

    // Then
    myCtx.assertIsSatisfied();
    then(content.trim().replace("\n", "").replace("\r", "")).isEqualTo(expectedContent.trim().replace("\n", "").replace("\r", ""));
  }

  @NotNull
  private CmdGenerator createInstance()
  {
    return new CmdGenerator(
      myCommandLineArgumentsService,
      myRunnerParametersService,
      myFileService);
  }
}
