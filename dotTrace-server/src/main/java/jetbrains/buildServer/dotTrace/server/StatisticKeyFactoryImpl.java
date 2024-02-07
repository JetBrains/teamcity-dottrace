

package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;

public class StatisticKeyFactoryImpl implements StatisticKeyFactory {
  public static final String DOT_TRACE_TOTAL_TIME_STATISTIC_KEY = "dot_trace_total_time";
  public static final String DOT_TRACE_OWN_TIME_STATISTIC_KEY = "dot_trace_own_time";

  @NotNull
  @Override
  public String createTotalTimeKey(@NotNull final String methodName) {
    return methodName + ":" + DOT_TRACE_TOTAL_TIME_STATISTIC_KEY;
  }

  @NotNull
  @Override
  public String createOwnTimeKey(@NotNull final String methodName) {
    return methodName + ":" + DOT_TRACE_OWN_TIME_STATISTIC_KEY;
  }
}