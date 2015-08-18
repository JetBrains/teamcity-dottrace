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
  private ResourceGenerator<DotTraceContext> myDotTraceProjectGenerator;
  private ResourcePublisher myBeforeBuildPublisher;
  private FileService myFileService;
  private ResourcePublisher myAfterBuildPublisher;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    //noinspection unchecked
    myDotTraceProjectGenerator = (ResourceGenerator<DotTraceContext>)myCtx.mock(ResourceGenerator.class);
    //noinspection unchecked
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
    myBeforeBuildPublisher = myCtx.mock(ResourcePublisher.class, "beforeBuildPublisher");
    myAfterBuildPublisher = myCtx.mock(ResourcePublisher.class, "afterBuildPublisher");
    myCommandLineResource = myCtx.mock(CommandLineResource.class);
    myFileService = myCtx.mock(FileService.class);
    myAssertions = myCtx.mock(RunnerAssertions.class);
  }

  @Test
  public void shouldCreateSetupWhenGetSetup()
  {
    // Given
    final CommandLineSetup baseSetup = new CommandLineSetup("someTool", Arrays.asList(new CommandLineArgument("/arg1", CommandLineArgument.Type.PARAMETER), new CommandLineArgument("/arg2", CommandLineArgument.Type.PARAMETER)), Collections.singletonList(myCommandLineResource));
    final File projectFile = new File("aaa");
    final File snapshotFile = new File("snapshot");
    final File dotTraceFile = new File("abc", DotTraceSetupBuilder.DOT_TRACE_EXE_NAME);
    myCtx.checking(new Expectations() {{
      oneOf(myAssertions).contains(RunnerAssertions.Assertion.PROFILING_IS_NOT_ALLOWED);
      will(returnValue(false));
      
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.PATH_VAR);
      will(returnValue(dotTraceFile.getParent()));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.USE_VAR);
      will(returnValue("True"));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.THRESHOLDS_VAR);
      will(returnValue(""));

      oneOf(myFileService).validatePath(new File("abc", DotTraceSetupBuilder.DOT_TRACE_EXE_NAME));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_PROJECT_EXT);
      will(returnValue(projectFile));

      oneOf(myDotTraceProjectGenerator).create(new DotTraceContext(baseSetup));
      will(returnValue("project's content"));

      oneOf(myFileService).getTempFileName(DotTraceSetupBuilder.DOT_TRACE_SNAPSHOT_EXT);
      will(returnValue(snapshotFile));
    }});

    final DotTraceSetupBuilder instance = createInstance();

    // When
    final CommandLineSetup setup = instance.build(baseSetup).iterator().next();

    // Then
    myCtx.assertIsSatisfied();
    then(setup.getToolPath()).isEqualTo(dotTraceFile.getPath());
    then(setup.getArgs()).containsExactly(new CommandLineArgument(projectFile.getPath(), CommandLineArgument.Type.PARAMETER), new CommandLineArgument(snapshotFile.getPath(), CommandLineArgument.Type.PARAMETER));
    then(setup.getResources()).containsExactly(myCommandLineResource, new CommandLineFile(myBeforeBuildPublisher, projectFile, "project's content"), new CommandLineArtifact(myAfterBuildPublisher, snapshotFile));
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
      myDotTraceProjectGenerator,
      myBeforeBuildPublisher,
      myAfterBuildPublisher,
      myRunnerParametersService,
      myFileService,
      myAssertions);
  }
}
