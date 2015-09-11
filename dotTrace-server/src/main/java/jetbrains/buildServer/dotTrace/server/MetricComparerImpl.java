package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class MetricComparerImpl implements MetricComparer {
  private static final BigDecimal MULTIPLICAND_100 = new BigDecimal(100);

  @Override
  public boolean isMeasuredValueWithinThresholds(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final ThresholdValue threshold) {
    switch (threshold.getType()) {
      case SKIPPED:
        return true;

      case ABSOLUTE:
        return threshold.getValue().compareTo(measuredValue) >= 0;

      case FIRST:
      case LAST:
      case AVERAGE:
        if(prevValue == null || prevValue.compareTo(BigDecimal.ZERO) == 0) {
          return true;
        }

        final BigDecimal deviation = measuredValue.subtract(prevValue).multiply(MULTIPLICAND_100).divide(prevValue, 4, RoundingMode.HALF_UP);
        return deviation.compareTo(threshold.getValue()) <= 0;

      default:
        throw new UnsupportedOperationException();
    }
  }
}
