package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DotTraceSetupBuilder implements CommandLineSetupBuilder {
  static final String DOT_TRACE_PROJECT_EXT = ".dotTrace.project.xml";
  static final String DOT_TRACE_PATTERNS_EXT = ".dotTrace.patterns.xml";
  static final String DOT_TRACE_REPORT_EXT = ".dotTrace.report.xml";
  static final String DOT_TRACE_CMD_EXT = ".cmd";
  static final String DOT_TRACE_SNAPSHOT_EXT = ".dtp";

  private final ResourceGenerator<Context> myProjectGenerator;
  private final ResourceGenerator<Context> myPatternGenerator;
  private final ResourceGenerator<Context> myCmdGenerator;
  private final ResourcePublisher myBeforeBuildPublisher;
  private final ResourcePublisher myDotTraceBuildPublisher;
  private final ResourcePublisher myDotTraceSnapshotsPublisher;
  private final RunnerParametersService myParametersService;
  private final FileService myFileService;
  private final RunnerAssertions myAssertions;

  public DotTraceSetupBuilder(
    @NotNull final ResourceGenerator<Context> projectGenerator,
    @NotNull final ResourceGenerator<Context> patternGenerator,
    @NotNull final ResourceGenerator<Context> cmdGenerator,
    @NotNull final ResourcePublisher beforeBuildPublisher,
    @NotNull final ResourcePublisher dotTraceBuildPublisher,
    @NotNull final ResourcePublisher dotTraceSnapshotsPublisher,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final FileService fileService,
    @NotNull final RunnerAssertions assertions) {
    myProjectGenerator = projectGenerator;
    myPatternGenerator = patternGenerator;
    myCmdGenerator = cmdGenerator;
    myBeforeBuildPublisher = beforeBuildPublisher;
    myDotTraceBuildPublisher = dotTraceBuildPublisher;
    myDotTraceSnapshotsPublisher = dotTraceSnapshotsPublisher;
    myParametersService = parametersService;
    myFileService = fileService;
    myAssertions = assertions;
  }

  @Override
  @NotNull
  public Iterable<CommandLineSetup> build(@NotNull final CommandLineSetup baseSetup) {
    if(myAssertions.contains(RunnerAssertions.Assertion.PROFILING_IS_NOT_ALLOWED)) {
      return Collections.singleton(baseSetup);
    }

    final String dotTraceTool = myParametersService.tryGetRunnerParameter(Constants.USE_VAR);
    if (StringUtil.isEmptyOrSpaces(dotTraceTool) || !Boolean.parseBoolean(dotTraceTool)) {
      return Collections.singleton(baseSetup);
    }

    final List<CommandLineResource> resources = new ArrayList<CommandLineResource>(baseSetup.getResources());
    final File cmdFile = myFileService.getTempFileName(DOT_TRACE_CMD_EXT);
    final File projectFile = myFileService.getTempFileName(DOT_TRACE_PROJECT_EXT);
    final File snapshotFile = myFileService.getTempFileName(DOT_TRACE_SNAPSHOT_EXT);
    final File patternsFile = myFileService.getTempFileName(DOT_TRACE_PATTERNS_EXT);
    final File reportFile = myFileService.getTempFileName(DOT_TRACE_REPORT_EXT);
    final Context ctx = new Context(baseSetup, projectFile, snapshotFile, patternsFile, reportFile);

    resources.add(new CommandLineFile(myBeforeBuildPublisher, projectFile, myProjectGenerator.create(ctx)));
    resources.add(new CommandLineFile(myBeforeBuildPublisher, patternsFile, myPatternGenerator.create(ctx)));
    resources.add(new CommandLineFile(myBeforeBuildPublisher, cmdFile, myCmdGenerator.create(ctx)));
    resources.add(new CommandLineArtifact(myDotTraceBuildPublisher, reportFile));
    resources.add(new CommandLineArtifact(myDotTraceSnapshotsPublisher, snapshotFile));

    return Collections.singleton(new CommandLineSetup(cmdFile.getPath(), Collections.<CommandLineArgument>emptyList(), resources));
  }
}