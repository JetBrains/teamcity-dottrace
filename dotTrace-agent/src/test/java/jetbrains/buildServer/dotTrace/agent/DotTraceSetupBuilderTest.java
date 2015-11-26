package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class DotTraceSetupBuilderTest {
  private Mockery myCtx;
  private RunnerParametersService myRunnerParametersService;
  private CommandLineResource myCommandLineResource;
  private RunnerAssertions myAssertions;
  private ResourceGenerator<Context> myProjectGenerator;
  private ResourcePublisher myBeforeBuildPublisher;
  private FileService myFileService;
  private ResourcePublisher myDotTraceBuildPublisher;
  private ResourceGenerator<Context> myPatternsGenerator;
  private ResourceGenerator<Context> myCmdGenerator;
  private ResourcePublisher myDotTraceSnapshotsPublisher;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    //noinspection unchecked
    myProjectGenerator = (ResourceGenerator<Context>)myCtx.mock(ResourceGenerator.class, "ProjectGenerator");
    //noinspection unchecked
    myPatternsGenerator = (ResourceGenerator<Context>)myCtx.mock(ResourceGenerator.class, "PatternsGenerator");
    //noinspection unchecked
    myCmdGenerator = (ResourceGenerator<Context>)myCtx.mock(ResourceGenerator.class, "CmdGenerator");
    //noinspection unchecked
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
    myBeforeBuildPublisher = myCtx.mock(ResourcePublisher.class, "beforeBuildPublisher");
    myDotTraceBuildPublisher = myCtx.mock(ResourcePublisher.class, "dotTraceBuildPublisher");
    myDotTraceSnapshotsPublisher = myCtx.mock(ResourcePublisher.class, "dotTraceSnapshotsPublisher");
    myCommandLineResource = myCtx.mock(CommandLineResource.class);
    myFileService = myCtx.mock(FileService.class);
    myAssertions = myCtx.mock(RunnerAssertions.class);
  }

  @Test
  public void shouldCreateSetupWhenGetSetup()
  {
    // Given
    final CommandLineSetup baseSetup = new CommandLineSetup("someTool", Arrays.asList(new CommandLineArgument("/arg1", CommandLineArgument.Type.PARAMETER), new CommandLineArgument("/arg2", CommandLineArgument.Type.PARAMETER)), Collections.singletonList(myCommandLineResource));
    final File cmdFile = new File("cmd");
    final File projectFile = new File("project");
    final File snapshotFile = new File("snapshot");
    final File patternsFile = new File("patterns");
    final File reportFile = new File("report");
    final Context ctx = new Context(baseSetup, projectFile, snapshotFile, patternsFile, reportFile);

    myCtx.checking(new Expectations() {{
      oneOf(myAssertions).contains(RunnerAssertions.Assertion.PROFILING_IS_NOT_ALLOWED);
      will(returnValue(false));
      
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.USE_VAR);
      will(returnValue("True"));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_CMD_EXT);
      will(returnValue(cmdFile));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_PROJECT_EXT);
      will(returnValue(projectFile));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_SNAPSHOT_EXT);
      will(returnValue(snapshotFile));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_PATTERNS_EXT);
      will(returnValue(patternsFile));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_REPORT_EXT);
      will(returnValue(reportFile));

      oneOf(myProjectGenerator).create(ctx);
      will(returnValue("project's content"));

      oneOf(myPatternsGenerator).create(ctx);
      will(returnValue("patterns' content"));

      oneOf(myCmdGenerator).create(ctx);
      will(returnValue("cmd's content"));
    }});

    final DotTraceSetupBuilder instance = createInstance();

    // When
    final CommandLineSetup setup = instance.build(baseSetup).iterator().next();

    // Then
    myCtx.assertIsSatisfied();
    then(setup.getToolPath()).isEqualTo(cmdFile.getPath());
    then(setup.getArgs()).isEmpty();
    then(setup.getResources()).containsExactly(
      myCommandLineResource,
      new CommandLineFile(myBeforeBuildPublisher, projectFile, "project's content"),
      new CommandLineFile(myBeforeBuildPublisher, patternsFile, "patterns' content"),
      new CommandLineFile(myBeforeBuildPublisher, cmdFile, "cmd's content"),
      new CommandLineArtifact(myDotTraceBuildPublisher, reportFile),
      new CommandLineArtifact(myDotTraceSnapshotsPublisher, snapshotFile));
  }

  @DataProvider(name = "runnerParamUseDotTraceCases")
  public Object[][] getParseTestsFromStringCases() {
    return new Object[][] {
      { null },
      { "" },
      { "abc" },
      { "False" },
      { "false" },
    };
  }

  @Test(dataProvider = "runnerParamUseDotTraceCases")
  public void shouldReturnBaseSetupWhenRunnerParamUseDotTraceIsEmptyOrFalse(final String useDotTrace)
  {
    // Given
    final CommandLineSetup baseSetup = new CommandLineSetup("someTool", Arrays.asList(new CommandLineArgument("/arg1", CommandLineArgument.Type.PARAMETER), new CommandLineArgument("/arg2", CommandLineArgument.Type.PARAMETER)), Collections.singletonList(myCommandLineResource));
    myCtx.checking(new Expectations() {{
      oneOf(myAssertions).contains(RunnerAssertions.Assertion.PROFILING_IS_NOT_ALLOWED);
      will(returnValue(false));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.USE_VAR);
      will(returnValue(useDotTrace));
    }});

    final DotTraceSetupBuilder instance = createInstance();

    // When
    final CommandLineSetup setup = instance.build(baseSetup).iterator().next();

    // Then
    myCtx.assertIsSatisfied();
    then(setup).isEqualTo(baseSetup);
  }

  @Test
  public void shouldReturnBaseSetupWhenProfilingIsNotAllowed()
  {
    // Given
    final CommandLineSetup baseSetup = new CommandLineSetup("someTool", Arrays.asList(new CommandLineArgument("/arg1", CommandLineArgument.Type.PARAMETER), new CommandLineArgument("/arg2", CommandLineArgument.Type.PARAMETER)), Collections.singletonList(myCommandLineResource));
    myCtx.checking(new Expectations() {{
      oneOf(myAssertions).contains(RunnerAssertions.Assertion.PROFILING_IS_NOT_ALLOWED);
      will(returnValue(true));
    }});

    final DotTraceSetupBuilder instance = createInstance();

    // When
    final CommandLineSetup setup = instance.build(baseSetup).iterator().next();

    // Then
    myCtx.assertIsSatisfied();
    then(setup).isEqualTo(baseSetup);
  }

  @NotNull
  private DotTraceSetupBuilder createInstance()
  {
    return new DotTraceSetupBuilder(
      myProjectGenerator,
      myPatternsGenerator,
      myCmdGenerator,
      myBeforeBuildPublisher,
      myDotTraceBuildPublisher,
      myDotTraceSnapshotsPublisher,
      myRunnerParametersService,
      myFileService,
      myAssertions);
  }
}
