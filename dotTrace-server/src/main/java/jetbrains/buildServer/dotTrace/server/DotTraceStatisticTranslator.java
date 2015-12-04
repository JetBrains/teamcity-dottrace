package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ServerExtensionHolder;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import org.jetbrains.annotations.NotNull;

public class DotTraceStatisticTranslator implements ServiceMessageTranslator {
  public static final String TOTAL_TIME_THRESHOLD_NAME = "Total Time";
  public static final String OWN_TIME_THRESHOLD_NAME = "Own Time";
  private final BuildDataStorage myStorage;
  private final MetricComparer myMetricComparer;
  private final StatisticKeyFactory myStatisticKeyFactory;
  private final StatisticProvider myStatisticProvider;
  private final History myHistory;

  public DotTraceStatisticTranslator(
    @NotNull final ServerExtensionHolder server,
    @NotNull final BuildDataStorage storage,
    @NotNull final MetricComparer metricComparer,
    @NotNull final StatisticKeyFactory statisticKeyFactory,
    @NotNull final StatisticProvider statisticProvider,
    @NotNull final History history) {
    myMetricComparer = metricComparer;
    myStorage = storage;
    myStatisticKeyFactory = statisticKeyFactory;
    myStatisticProvider = statisticProvider;
    myHistory = history;
    server.registerExtension(ServiceMessageTranslator.class, getClass().getName(), this);
  }

  @NotNull
  @Override
  public String getServiceMessageName() {
    return StatisticMessage.MESSAGE_NAME;
  }

  @NotNull
  @Override
  public List<BuildMessage1> translate(@NotNull final SRunningBuild runningBuild, @NotNull final BuildMessage1 buildMessage, @NotNull final ServiceMessage serviceMessage) {
    List<BuildMessage1> messages = new Vector<BuildMessage1>(Collections.singleton(buildMessage));

    final StatisticMessage statisticMessage = StatisticMessage.tryParse(serviceMessage);
    if(statisticMessage == null) {
      return messages;
    }

    final SBuildType buildType = runningBuild.getBuildType();
    if(buildType == null) {
      return messages;
    }

    final String methodName = statisticMessage.getMethodName();
    final Statistic statistic = myStatisticProvider.tryCreateStatistic(statisticMessage, myHistory.getElements(buildType.getHistory()));
    if(statistic != null) {
      final BigDecimal prevTotalTime = statistic.getPrevTotalTime();
      if (prevTotalTime != null && !myMetricComparer.isMeasuredValueWithinThresholds(prevTotalTime, statistic.getMeasuredTotalTime(), statistic.getTotalTimeThreshold())) {
        final BigDecimal expectedValue = myMetricComparer.tryGetThresholdValue(prevTotalTime, statistic.getTotalTimeThreshold());
        if(expectedValue != null) {
          messages.add(
            new BuildMessage1(
              buildMessage.getSourceId(),
              buildMessage.getTypeId(),
              Status.FAILURE,
              buildMessage.getTimestamp(),
              createBuildMessageText(expectedValue, statistic.getMeasuredTotalTime(), methodName, TOTAL_TIME_THRESHOLD_NAME)));
        }
      }

      final BigDecimal prevOwnTime = statistic.getPrevOwnTime();
      if (prevOwnTime != null && !myMetricComparer.isMeasuredValueWithinThresholds(prevOwnTime, statistic.getMeasuredOwnTime(), statistic.getOwnTimeThreshold())) {
        final BigDecimal expectedValue = myMetricComparer.tryGetThresholdValue(prevOwnTime, statistic.getOwnTimeThreshold());
        if(expectedValue != null) {
          messages.add(
            new BuildMessage1(
              buildMessage.getSourceId(),
              buildMessage.getTypeId(),
              Status.FAILURE,
              buildMessage.getTimestamp(),
              createBuildMessageText(expectedValue, statistic.getMeasuredOwnTime(), methodName, OWN_TIME_THRESHOLD_NAME)));
        }
      }

      final long buildId = runningBuild.getBuildId();
      myStorage.publishValue(myStatisticKeyFactory.createTotalTimeKey(methodName), buildId, statistic.getMeasuredTotalTime());
      myStorage.publishValue(myStatisticKeyFactory.createOwnTimeKey(methodName), buildId, statistic.getMeasuredOwnTime());
    }

    return messages;
  }

  @NotNull
  private String createBuildMessageText(@NotNull final BigDecimal expectedValue, @NotNull final BigDecimal measuredValue, @NotNull final String methodName, @NotNull final String valueDescription) {
    return String.format("FAILED: %s\n\t%s, ms [expected: %s | measured: %s]", methodName, valueDescription, expectedValue, measuredValue);
  }
}