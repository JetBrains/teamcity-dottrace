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
import java.util.Arrays;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;

public class CmdGenerator implements ResourceGenerator<Context> {
  static final String DOT_TRACE_EXE_NAME = "ConsoleProfiler.exe";
  static final String DOT_TRACE_REPORTER_EXE_NAME = "Reporter.exe";

  private static final String ourLineSeparator = System.getProperty("line.separator");

  private final CommandLineArgumentsService myCommandLineArgumentsService;
  private final RunnerParametersService myParametersService;
  private final FileService myFileService;

  public CmdGenerator(
    @NotNull final CommandLineArgumentsService commandLineArgumentsService,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final FileService fileService) {
    myCommandLineArgumentsService = commandLineArgumentsService;
    myParametersService = parametersService;
    myFileService = fileService;
  }

  @NotNull
  @Override
  public String create(@NotNull final Context ctx) {
    File dotTracePath = new File(myParametersService.getRunnerParameter(Constants.PATH_VAR));
    if(!dotTracePath.isAbsolute()) {
      dotTracePath = new File(myFileService.getCheckoutDirectory(), dotTracePath.getPath());
    }

    File consoleProfilerFile = new File(dotTracePath, DOT_TRACE_EXE_NAME);
    myFileService.validatePath(consoleProfilerFile);

    File reporterPath = new File(dotTracePath, DOT_TRACE_REPORTER_EXE_NAME);
    myFileService.validatePath(reporterPath);

    final StringBuilder cmdLines = new StringBuilder();

    // Run profiler
    cmdLines.append(
      myCommandLineArgumentsService.createCommandLineString(
        Arrays.asList(
          new CommandLineArgument(consoleProfilerFile.getPath(), CommandLineArgument.Type.TOOL),
          new CommandLineArgument(ctx.getProjectFile().getPath(), CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(ctx.getSnapshotFile().getPath(), CommandLineArgument.Type.PARAMETER))));

    cmdLines.append(ourLineSeparator);

    // Save exit code to EXIT_CODE
    cmdLines.append("@SET EXIT_CODE=%ERRORLEVEL%");
    cmdLines.append(ourLineSeparator);

    // Run reporter
    File stapshotDir = ctx.getSnapshotFile().getParentFile();
    if(stapshotDir == null) {
      stapshotDir = new File(".");
    }
    cmdLines.append(
      myCommandLineArgumentsService.createCommandLineString(
        Arrays.asList(
          new CommandLineArgument(reporterPath.getPath(), CommandLineArgument.Type.TOOL),
          new CommandLineArgument("report", CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(stapshotDir.getPath() + File.separator + "*.dtp", CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument("--pattern=" + ctx.getPatternsFile().getPath(), CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument("--save-to=" + ctx.getReportFile().getPath(), CommandLineArgument.Type.PARAMETER))));

    cmdLines.append(ourLineSeparator);

    // Return exit code from EXIT_CODE
    cmdLines.append("@EXIT %EXIT_CODE%");
    cmdLines.append(ourLineSeparator);

    return cmdLines.toString();
  }
}