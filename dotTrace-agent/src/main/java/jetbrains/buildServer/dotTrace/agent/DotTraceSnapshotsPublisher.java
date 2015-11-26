package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineExecutionContext;
import jetbrains.buildServer.dotNet.buildRunner.agent.LoggerService;
import jetbrains.buildServer.dotNet.buildRunner.agent.ResourcePublisher;
import jetbrains.buildServer.dotNet.buildRunner.agent.RunnerParametersService;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.messages.serviceMessages.PublishArtifacts;
import org.jetbrains.annotations.NotNull;

public class DotTraceSnapshotsPublisher implements ResourcePublisher {
  private final LoggerService myLoggerService;
  private final RunnerParametersService myParametersService;

  public DotTraceSnapshotsPublisher(
    @NotNull final LoggerService loggerService,
    @NotNull final RunnerParametersService parametersService) {
    myLoggerService = loggerService;
    myParametersService = parametersService;
  }

  @Override
  public void publishBeforeBuildFile(@NotNull final CommandLineExecutionContext executionContext, @NotNull final File file, @NotNull final String content) {
  }

  @Override
  public void publishAfterBuildArtifactFile(@NotNull final CommandLineExecutionContext executionContext, @NotNull final File file) {
    final String snapshotsTargetDirectoryStr = myParametersService.tryGetRunnerParameter(Constants.SNAPSHOTS_PATH_VAR);
    if(StringUtil.isEmptyOrSpaces(snapshotsTargetDirectoryStr)) {
      return;
    }

    final File snapshotsTargetDirectory = new File(snapshotsTargetDirectoryStr);
    final String artifactPath = String.format("%s => %s", file.getPath(), snapshotsTargetDirectory.getPath());
    myLoggerService.onMessage(new PublishArtifacts(artifactPath));
  }
}
