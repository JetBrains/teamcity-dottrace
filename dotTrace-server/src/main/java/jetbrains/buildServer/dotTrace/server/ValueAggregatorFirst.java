package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ValueAggregatorFirst implements ValueAggregator {
  private boolean myIsCompleted = false;
  private BigDecimal myVal = null;

  @Override
  public void aggregate(@NotNull final BigDecimal value) {
    if(myIsCompleted) {
      return;
    }

    myIsCompleted = true;
    myVal = value;
  }

  @Override
  public boolean isCompleted() {
    return myIsCompleted;
  }

  @Nullable
  @Override
  public BigDecimal tryGetAggregatedValue() {
    return myVal;
  }
}
