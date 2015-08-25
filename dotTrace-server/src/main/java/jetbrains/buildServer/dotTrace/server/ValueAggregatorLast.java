package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class ValueAggregatorLast implements ValueAggregator {
  private BigDecimal myVal = null;

  @Override
  public void aggregate(@NotNull final BigDecimal value) {
    myVal = value;
  }

  @Override
  public boolean isCompleted() {
    return false;
  }

  @Nullable
  @Override
  public BigDecimal tryGetAggregatedValue() {
    return myVal;
  }
}
