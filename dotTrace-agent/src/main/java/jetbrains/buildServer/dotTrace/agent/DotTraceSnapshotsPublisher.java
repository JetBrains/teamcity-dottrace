/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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