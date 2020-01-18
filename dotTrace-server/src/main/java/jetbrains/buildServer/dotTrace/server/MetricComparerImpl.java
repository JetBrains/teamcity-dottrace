/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.jetbrains.annotations.NotNull;

public class MetricComparerImpl implements MetricComparer {
  private static final BigDecimal MULTIPLICAND_100 = new BigDecimal(100);

  @Override
  public boolean isMeasuredValueWithinThresholds(@NotNull final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final ThresholdValue threshold) {
    final BigDecimal thresholdValue = tryGetThresholdValue(prevValue, threshold);
    if(thresholdValue == null) {
      return true;
    }

    return thresholdValue.compareTo(measuredValue) >= 0;
  }

  public BigDecimal tryGetThresholdValue(@NotNull final BigDecimal prevValue, @NotNull final ThresholdValue threshold) {
    switch (threshold.getType()) {
      case SKIPPED:
        return null;

      case ABSOLUTE:
        return threshold.getValue();

      case FIRST:
      case LAST:
      case AVERAGE:
        if(prevValue.compareTo(BigDecimal.ZERO) == 0) {
          return null;
        }

        final BigDecimal deviation = prevValue.multiply(threshold.getValue()).divide(MULTIPLICAND_100, 4, RoundingMode.HALF_UP);
        return prevValue.add(deviation);

      default:
        throw new UnsupportedOperationException();
    }
  }
}
