package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.messages.serviceMessages.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DotTraceSnapshotsPublisherTest {
  private Mockery myCtx;
  private LoggerService myLoggerService;
  private RunnerParametersService myRunnerParametersService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();
    //noinspection unchecked
    myLoggerService = myCtx.mock(LoggerService.class);
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
  }

  @Test
  public void shouldSendPerformanceSnapshotFileAsArtifactFilesWhenPublishAfterBuildArtifactFile() throws IOException {
    // Given
    final CommandLineExecutionContext executionContext = new CommandLineExecutionContext(0);
    final File outputFile = new File("output");
    final File snapshotsDir = new File("snapshotsDir");

    myCtx.checking(new Expectations() {{
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.SNAPSHOTS_PATH_VAR);
      will(returnValue(snapshotsDir.getPath()));

      oneOf(myLoggerService).onMessage(with(any(Message.class)));
    }});

    final ResourcePublisher instance = createInstance();

    // When
    instance.publishAfterBuildArtifactFile(executionContext, outputFile);

    // Then
    myCtx.assertIsSatisfied();
  }

  @DataProvider(name = "artefactsPathCases")
  public Object[][] getArtefactsPathCases() {
    return new Object[][] {
      { null },
      { "" },
      { "  " },
    };
  }

  @Test(dataProvider = "artefactsPathCases")
  public void shouldNotSendPerformanceSnapshotFileAsArtifactFilesWhenSnapshotsPathIsNullOrEmpty(@Nullable final String artefactsPath) throws IOException {
    // Given
    final CommandLineExecutionContext executionContext = new CommandLineExecutionContext(0);
    final File outputFile = new File("output");

    myCtx.checking(new Expectations() {{
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.SNAPSHOTS_PATH_VAR);
      will(returnValue(artefactsPath));

      never(myLoggerService).onMessage(with(any(Message.class)));
    }});

    final ResourcePublisher instance = createInstance();

    // When
    instance.publishAfterBuildArtifactFile(executionContext, outputFile);

    // Then
    myCtx.assertIsSatisfied();
  }

  @NotNull
  private DotTraceSnapshotsPublisher createInstance()
  {
    return new DotTraceSnapshotsPublisher(myLoggerService, myRunnerParametersService);
  }
}
