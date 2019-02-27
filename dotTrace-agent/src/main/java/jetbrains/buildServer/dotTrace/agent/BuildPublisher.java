package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import org.jetbrains.annotations.NotNull;

public class BuildPublisher implements ResourcePublisher {
  private final TextParser<Metrics> myReportParser;
  private final TextParser<Metrics> myThresholdsParser;
  private final ResourcePublisher myAfterBuildPublisher;
  private final RunnerParametersService myParametersService;
  private final FileService myFileService;
  private final LoggerService myLoggerService;

  public BuildPublisher(
    @NotNull final TextParser<Metrics> reportParser,
    @NotNull final TextParser<Metrics> thresholdsParser,
    @NotNull final ResourcePublisher afterBuildPublisher,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final FileService fileService,
    @NotNull final LoggerService loggerService) {
    myReportParser = reportParser;
    myThresholdsParser = thresholdsParser;
    myAfterBuildPublisher = afterBuildPublisher;
    myParametersService = parametersService;
    myFileService = fileService;
    myLoggerService = loggerService;
  }

  @Override
  public void publishBeforeBuildFile(@NotNull final CommandLineExecutionContext commandLineExecutionContext, @NotNull final File file, @NotNull final String content) {
  }

  @Override
  public void publishAfterBuildArtifactFile(@NotNull final CommandLineExecutionContext commandLineExecutionContext, @NotNull final File reportFile) {
    myAfterBuildPublisher.publishAfterBuildArtifactFile(commandLineExecutionContext, reportFile);

    Metrics thresholdValues;
    String thresholdsStr = myParametersService.tryGetRunnerParameter(Constants.THRESHOLDS_VAR);
    if(!StringUtil.isEmptyOrSpaces(thresholdsStr)) {
      thresholdValues = myThresholdsParser.parse(thresholdsStr);
    }
    else {
      return;
    }

    try {
      final String reportContent = myFileService.readAllTextFile(reportFile);
      final Metrics measuredValues = myReportParser.parse(reportContent);
      for (Metric measuredValue : measuredValues.getMetrics()) {
        for (final Metric thresholdValue : thresholdValues.getMetrics()) {
          if(measuredValue.getMethodName().startsWith(thresholdValue.getMethodName())) {
            myLoggerService.onMessage(new StatisticMessage(measuredValue.getMethodName(), thresholdValue.getTotalTime(), thresholdValue.getOwnTime(), measuredValue.getTotalTime(), measuredValue.getOwnTime()));
          }
        }
      }
    }
    catch (IOException e) {
      throw new BuildException(e.getMessage());
    }
  }
}