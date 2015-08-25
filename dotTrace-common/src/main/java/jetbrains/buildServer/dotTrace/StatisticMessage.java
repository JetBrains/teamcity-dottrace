package jetbrains.buildServer.dotTrace;

import java.util.Map;
import jetbrains.buildServer.messages.serviceMessages.MessageWithAttributes;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.util.CollectionsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatisticMessage extends MessageWithAttributes {
  public static final String MESSAGE_NAME = "dotTraceStatistic";
  private static final String METHOD_STATISTIC_MESSAGE_ATTR = "methodName";
  private static final String THRESHOLD_TOTAL_TIME_STATISTIC_MESSAGE_ATTR = "totalTimeThreshold";
  private static final String THRESHOLD_OWN_TIME_STATISTIC_MESSAGE_ATTR = "ownTimeThreshold";
  private static final String MEASURED_TOTAL_TIME_STATISTIC_MESSAGE_ATTR = "measuredTotalTime";
  private static final String MEASURED_OWN_TIME_STATISTIC_MESSAGE_ATTR = "measuredOwnTime";

  public StatisticMessage(
    @NotNull final String methodName,
    @NotNull final String totalTimeThreshold,
    @NotNull final String ownTimeThreshold,
    @NotNull final String measuredTotalTime,
    @NotNull final String measuredOwnTime) {
    super(
      MESSAGE_NAME,
      CollectionsUtil.asMap(
        METHOD_STATISTIC_MESSAGE_ATTR, methodName,
        THRESHOLD_TOTAL_TIME_STATISTIC_MESSAGE_ATTR, totalTimeThreshold,
        THRESHOLD_OWN_TIME_STATISTIC_MESSAGE_ATTR, ownTimeThreshold,
        MEASURED_TOTAL_TIME_STATISTIC_MESSAGE_ATTR, measuredTotalTime,
        MEASURED_OWN_TIME_STATISTIC_MESSAGE_ATTR, measuredOwnTime));
  }

  private StatisticMessage(@NotNull final Map<String, String> attributes) {
    super(MESSAGE_NAME, attributes);
  }

  @Nullable
  public static StatisticMessage tryParse(@NotNull final ServiceMessage message) {
    if(!MESSAGE_NAME.equalsIgnoreCase(message.getMessageName())) {
      return null;
    }

    return new StatisticMessage(message.getAttributes());
  }

  @NotNull
  public String getMethodName() {
    return getAttributes().get(METHOD_STATISTIC_MESSAGE_ATTR);
  }

  @NotNull
  public String getOwnTimeThreshold() {
    return getAttributes().get(THRESHOLD_OWN_TIME_STATISTIC_MESSAGE_ATTR);
  }

  @NotNull
  public String getTotalTimeThreshold() {
    return getAttributes().get(THRESHOLD_TOTAL_TIME_STATISTIC_MESSAGE_ATTR);
  }

  @NotNull
  public String getMeasuredOwnTime() {
    return getAttributes().get(MEASURED_OWN_TIME_STATISTIC_MESSAGE_ATTR);
  }

  @NotNull
  public String getMeasuredTotalTime() {
    return getAttributes().get(MEASURED_TOTAL_TIME_STATISTIC_MESSAGE_ATTR);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final StatisticMessage statisticMessage = (StatisticMessage)o;

    if (!getMethodName().equals(statisticMessage.getMethodName())) return false;
    if (!getTotalTimeThreshold().equals(statisticMessage.getTotalTimeThreshold())) return false;
    if (!getOwnTimeThreshold().equals(statisticMessage.getOwnTimeThreshold())) return false;
    if (!getMeasuredTotalTime().equals(statisticMessage.getMeasuredTotalTime())) return false;
    return getMeasuredOwnTime().equals(statisticMessage.getMeasuredOwnTime());

  }

  @Override
  public int hashCode() {
    int result = getMethodName().hashCode();
    result = 31 * result + getTotalTimeThreshold().hashCode();
    result = 31 * result + getOwnTimeThreshold().hashCode();
    result = 31 * result + getMeasuredTotalTime().hashCode();
    result = 31 * result + getMeasuredOwnTime().hashCode();
    return result;
  }
}