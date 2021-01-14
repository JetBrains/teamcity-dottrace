/*
 * Copyright 2000-2021 JetBrains s.r.o.
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
import org.assertj.core.data.Offset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class MetricComparerTest {
  @DataProvider(name = "checkMeasuredValueWithinThresholdsCases")
  public Object[][] getCheckMeasuredValueWithinThresholdsCases() {
    return new Object[][] {
      { new BigDecimal(100), new BigDecimal(111), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), false },
      { new BigDecimal(100), new BigDecimal(110), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(109), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(111), new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(10)), false },
      { new BigDecimal(100), new BigDecimal(110), new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(109), new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(111), new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(10)), false },
      { new BigDecimal(100), new BigDecimal(110), new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(109), new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(101), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(100)), false },
      { new BigDecimal(100), new BigDecimal(100), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(100)), true },
      { new BigDecimal(100), new BigDecimal(99), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(100)), true },
      { new BigDecimal(0), new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(30)), true },
      { new BigDecimal(100), new BigDecimal(120), new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(10)), true },
    };
  }

  @Test(dataProvider = "checkMeasuredValueWithinThresholdsCases")
  public void shouldCheckMeasuredValueWithinThresholds(@NotNull final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final ThresholdValue threshold, boolean expectedResult)
  {
    // Given
    final MetricComparer instance = createInstance();

    // When
    final boolean actualResult = instance.isMeasuredValueWithinThresholds(prevValue, measuredValue, threshold);

    // Then

    then(actualResult).isEqualTo(expectedResult);
  }

  @DataProvider(name = "thresholdValueCases")
  public Object[][] getThresholdValueCases() {
    return new Object[][] {
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), new BigDecimal(110) },
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(10)), new BigDecimal(110) },
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(10)), new BigDecimal(110) },
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(110)), new BigDecimal(110) },
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(120)), new BigDecimal(120) },
      { new BigDecimal(100), new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(10)), null },
      { BigDecimal.ZERO, new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), null },
      { BigDecimal.ZERO, new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(10)), null },
      { BigDecimal.ZERO, new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(10)), null },
    };
  }

  @Test(dataProvider = "thresholdValueCases")
  public void shouldGetThresholdValue(@NotNull final BigDecimal prevValue, @NotNull final ThresholdValue threshold, @Nullable final BigDecimal expectedThresholdValue)
  {
    // Given
    final MetricComparer instance = createInstance();

    // When
    final BigDecimal actualThresholdValue = instance.tryGetThresholdValue(prevValue, threshold);

    // Then

    if(expectedThresholdValue == null) {
      then(actualThresholdValue).isNull();
    }
    else {
      then(actualThresholdValue).isCloseTo(expectedThresholdValue, Offset.offset(new BigDecimal(.0001)));
    }
  }

  @NotNull
  private MetricComparer createInstance()
  {
    return new MetricComparerImpl();
  }
}
