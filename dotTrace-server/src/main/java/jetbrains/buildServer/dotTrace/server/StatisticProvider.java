package jetbrains.buildServer.dotTrace.server;

import javax.annotation.Nullable;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import org.jetbrains.annotations.NotNull;

public interface StatisticProvider {
  @Nullable Statistic tryCreateStatistic(@NotNull final StatisticMessage statisticMessage, @NotNull final Iterable<HistoryElement> history);
}
