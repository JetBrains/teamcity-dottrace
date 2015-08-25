package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface ValueAggregator {
  void aggregate(@NotNull final BigDecimal value);

  boolean isCompleted();

  @Nullable
  BigDecimal tryGetAggregatedValue();
}
