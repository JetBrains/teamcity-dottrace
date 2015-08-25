package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class MetricComparerImpl implements MetricComparer {
  private static final BigDecimal MULTIPLICAND_100 = new BigDecimal(100);

  @Override
  public boolean isMeasuredValueWithinThresholds(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final BigDecimal thresholdValue) {
    if(prevValue == null || prevValue.compareTo(BigDecimal.ZERO) == 0) {
      return true;
    }

    final BigDecimal deviation = measuredValue.subtract(prevValue).multiply(MULTIPLICAND_100).divide(prevValue, 4, RoundingMode.HALF_UP);
    return deviation.compareTo(thresholdValue) <= 0;
  }
}
