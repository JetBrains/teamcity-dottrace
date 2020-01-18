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
  private FileService myFileService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();
    //noinspection unchecked
    myLoggerService = myCtx.mock(LoggerService.class);
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
    myFileService = myCtx.mock(FileService.class);
  }

  @Test
  public void shouldSendSnapshotFileAsArtifactFileWhenHasNoParentDirectory() throws IOException {
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

  @Test
  public void shouldSendAllSnapshotsFileFromDirectoryWhenHasParentDirectory() throws IOException {
    // Given
    final CommandLineExecutionContext executionContext = new CommandLineExecutionContext(0);
    final File root = new File("root");
    final File outputFile = new File(root, "output");
    final File snapshotsDir = new File("snapshotsDir");

    myCtx.checking(new Expectations() {{
      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.SNAPSHOTS_PATH_VAR);
      will(returnValue(snapshotsDir.getPath()));

      oneOf(myFileService).listFiles(root);
      will(returnValue(new File[] { new File("snapshot1"), new File("snapshot2") }));

      exactly(2).of(myLoggerService).onMessage(with(any(Message.class)));
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
  public void shouldNotSendSnapshotFileAsArtifactFilesWhenSnapshotsPathIsNullOrEmpty(@Nullable final String artefactsPath) throws IOException {
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
    return new DotTraceSnapshotsPublisher(
      myLoggerService,
      myRunnerParametersService,
      myFileService);
  }
}
