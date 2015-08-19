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
  private static final String REPORTING_ARG = "/reporting";
  private static final String SET_EXIT_CODE_CMD = "SET EXIT_CODE=%ERRORLEVEL%";
  private static final String ECHO_EXIT_CODE_CMD = "@echo EXIT_CODE=%EXIT_CODE%";
  private static final String EXIT_CMD = "exit %EXIT_CODE%";

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
  public String create(final Context ctx) {
    String dotTracePath = myParametersService.getRunnerParameter(Constants.PATH_VAR);

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
    cmdLines.append(SET_EXIT_CODE_CMD);
    cmdLines.append(ourLineSeparator);

    // Run reporter
    cmdLines.append(
      myCommandLineArgumentsService.createCommandLineString(
        Arrays.asList(
          new CommandLineArgument(reporterPath.getPath(), CommandLineArgument.Type.TOOL),
          new CommandLineArgument(REPORTING_ARG, CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(ctx.getSnapshotFile().getPath(), CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(ctx.getPatternsFile().getPath(), CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(ctx.getReportFile().getPath(), CommandLineArgument.Type.PARAMETER))));

    cmdLines.append(ourLineSeparator);

    // Return exit code from EXIT_CODE
    cmdLines.append(ECHO_EXIT_CODE_CMD);
    cmdLines.append(ourLineSeparator);
    cmdLines.append(EXIT_CMD);
    cmdLines.append(ourLineSeparator);

    return cmdLines.toString();
  }
}