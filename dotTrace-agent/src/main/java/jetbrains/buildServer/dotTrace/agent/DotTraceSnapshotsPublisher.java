

package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.util.*;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.messages.serviceMessages.PublishArtifacts;
import org.jetbrains.annotations.NotNull;

public class DotTraceSnapshotsPublisher implements ResourcePublisher {
  private final LoggerService myLoggerService;
  private final RunnerParametersService myParametersService;
  private final FileService myFileService;

  public DotTraceSnapshotsPublisher(
    @NotNull final LoggerService loggerService,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final FileService fileService) {
    myLoggerService = loggerService;
    myParametersService = parametersService;
    myFileService = fileService;
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

    final List<File> snapshots = new ArrayList<File>();
    final File parentDir = file.getParentFile();
    if(parentDir == null) {
      snapshots.add(file);
    }
    else {
      snapshots.addAll(Arrays.asList(myFileService.listFiles(parentDir)));
    }

    final File snapshotsTargetDirectory = new File(snapshotsTargetDirectoryStr);
    for(File snapshot: snapshots) {
      final String artifactPath = String.format("%s => %s", snapshot.getPath(), snapshotsTargetDirectory.getPath());
      myLoggerService.onMessage(new PublishArtifacts(artifactPath));
    }
  }
}