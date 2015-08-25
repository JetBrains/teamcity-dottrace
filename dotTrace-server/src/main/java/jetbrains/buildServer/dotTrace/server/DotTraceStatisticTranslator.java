package jetbrains.buildServer.dotTrace.server;

import com.intellij.openapi.util.text.StringUtil;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.annotation.Nullable;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ServerExtensionHolder;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;

public class DotTraceStatisticTranslator implements ServiceMessageTranslator {
  private static final BigDecimal MULTIPLICAND_100 = new BigDecimal(100);
  public static final String DOT_TRACE_TOTAL_TIME_STATISTIC_KEY = "dot_trace_total_time";
  public static final String DOT_TRACE_OWN_TIME_STATISTIC_KEY = "dot_trace_own_time";
  public static final String TOTAL_TIME_THRESHOLD_NAME = "Total Time";
  public static final String OWN_TIME_THRESHOLD_NAME = "Own Time";
  private final BuildDataStorage myStorage;
  private BeanFactory myBeanFactory;

  public DotTraceStatisticTranslator(
    @NotNull final ServerExtensionHolder server,
    @NotNull final BuildDataStorage storage,
    @NotNull final BeanFactory beanFactory) {
    server.registerExtension(ServiceMessageTranslator.class, getClass().getName(), this);
    myStorage = storage;
    myBeanFactory = beanFactory;
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
    @Nullable final BigDecimal measuredTotalTime = tryParseBiDecimal(statisticMessage.getMeasuredTotalTime());
    @Nullable final BigDecimal measuredOwnTime = tryParseBiDecimal(statisticMessage.getMeasuredOwnTime());
    @Nullable final ThresholdValue totalTimeThreshold = ThresholdValue.tryParse(statisticMessage.getTotalTimeThreshold());
    @Nullable final ThresholdValue ownTimeThreshold = ThresholdValue.tryParse(statisticMessage.getOwnTimeThreshold());
    if(measuredTotalTime == null || measuredOwnTime == null || totalTimeThreshold == null || ownTimeThreshold == null) {
      return messages;
    }

    final ValueAggregatorFactory valueAggregatorFactory = myBeanFactory.getBean(ValueAggregatorFactory.class);
    final String totalTimeKey = createKey(methodName, DOT_TRACE_TOTAL_TIME_STATISTIC_KEY);
    final String ownTimeKey = createKey(methodName, DOT_TRACE_OWN_TIME_STATISTIC_KEY);
    final ValueAggregator totalTimeAgg = valueAggregatorFactory.create(totalTimeThreshold.getType());
    final ValueAggregator ownTimeAgg = valueAggregatorFactory.create(ownTimeThreshold.getType());

    for(SFinishedBuild build: buildType.getHistory()) {
      if(!build.getBuildStatus().isSuccessful()) {
        continue;
      }

      final Map<String, BigDecimal> values = myStorage.getValues(build);
      totalTimeAgg.aggregate(values.get(totalTimeKey));
      ownTimeAgg.aggregate(values.get(ownTimeKey));

      if(totalTimeAgg.isCompleted() && ownTimeAgg.isCompleted()) {
        break;
      }
    }

    @Nullable final BigDecimal prevTotalTime = totalTimeAgg.tryGetAggregatedValue();
    @Nullable final BigDecimal prevOwnTime = ownTimeAgg.tryGetAggregatedValue();

    if(!isMeasuredValueWithinThresholds(prevTotalTime, measuredTotalTime, totalTimeThreshold.getValue())) {
      messages.add(
        new BuildMessage1(
          buildMessage.getSourceId(),
          buildMessage.getTypeId(),
          Status.FAILURE,
          buildMessage.getTimestamp(),
          createBuildMessageText(prevTotalTime, measuredTotalTime, statisticMessage.getTotalTimeThreshold(), methodName, TOTAL_TIME_THRESHOLD_NAME)));
    }

    if(!isMeasuredValueWithinThresholds(prevOwnTime, measuredOwnTime, ownTimeThreshold.getValue())) {
      messages.add(
        new BuildMessage1(
          buildMessage.getSourceId(),
          buildMessage.getTypeId(),
          Status.FAILURE,
          buildMessage.getTimestamp(),
          createBuildMessageText(prevOwnTime, measuredOwnTime, statisticMessage.getOwnTimeThreshold(), methodName, OWN_TIME_THRESHOLD_NAME)));
    }

    myStorage.publishValue(totalTimeKey, runningBuild.getBuildId(), measuredTotalTime);
    myStorage.publishValue(ownTimeKey, runningBuild.getBuildId(), measuredOwnTime);

    return messages;
  }

  @NotNull
  private static String createKey(@NotNull final String methodName, @NotNull final String valueKey) {
    return methodName + ":" + valueKey;
  }

  @Nullable
  private static BigDecimal tryParseBiDecimal(@Nullable final String valueStr) {
    if(StringUtil.isEmptyOrSpaces(valueStr)) {
      return null;
    }

    try {
      return new BigDecimal(valueStr);
    }
    catch (NumberFormatException ignored) {
      return null;
    }
  }

  private boolean isMeasuredValueWithinThresholds(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final BigDecimal thresholdValue) {
    if(prevValue == null) {
      return true;
    }

    final BigDecimal deviation = measuredValue.subtract(prevValue).multiply(MULTIPLICAND_100).divide(prevValue).abs();
    return deviation.compareTo(thresholdValue) <= 0;
  }

  @NotNull
  private String createBuildMessageText(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final String threshold, @NotNull final String methodName, @NotNull final String valueDescription) {
    if(prevValue == null) {
      return String.format("%s exceeded the performance threshold \"%s\" for %s", methodName, threshold, valueDescription);
    }

    final String prevValueStr = prevValue.toString();
    final String measuredValueStr = measuredValue.toString();
    return String.format("%s exceeded the performance threshold \"%s\" for %s: the base value is %s, actual value is %s", methodName, threshold, valueDescription, prevValueStr, measuredValueStr);
  }
}