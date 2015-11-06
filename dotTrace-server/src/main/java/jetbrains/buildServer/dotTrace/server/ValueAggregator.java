package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public interface ValueAggregator {
  void aggregate(@NotNull final BigDecimal value);

  boolean isCompleted();

  @Nullable
  BigDecimal tryGetAggregatedValue();
}
