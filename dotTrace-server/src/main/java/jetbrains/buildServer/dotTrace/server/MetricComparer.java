package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface MetricComparer {
  boolean isMeasuredValueWithinThresholds(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final BigDecimal thresholdValue);
}
