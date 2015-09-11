package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class ValueAggregatorSkipped implements ValueAggregator {
  @Override
  public void aggregate(@NotNull final BigDecimal value) {
  }

  @Override
  public boolean isCompleted() {
    return true;
  }

  @Nullable
  @Override
  public BigDecimal tryGetAggregatedValue() {
    return null;
  }
}
