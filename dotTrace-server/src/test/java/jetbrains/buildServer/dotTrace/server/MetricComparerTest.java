package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class MetricComparerTest {
  @DataProvider(name = "checkMeasuredValueWithinThresholdsCases")
  public Object[][] getCheckMeasuredValueWithinThresholds() {
    return new Object[][] {
      { new BigDecimal(100), new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(30)), true },
      { new BigDecimal(113), new BigDecimal(202), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), false },
      { new BigDecimal(100), new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), false },
      { new BigDecimal(100), new BigDecimal(50), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(20)), true },
      { null, new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(30)), true },
      { new BigDecimal(0), new BigDecimal(120), new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(30)), true },
      { null, new BigDecimal(8), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(10)), true },
      { null, new BigDecimal(10), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(10)), true },
      { null, new BigDecimal(12), new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(10)), false },
      { null, new BigDecimal(20), new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(10)), true },
      { null, new BigDecimal(8), new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(10)), true },
      { new BigDecimal(100), new BigDecimal(120), new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(10)), true },
    };
  }

  @Test(dataProvider = "checkMeasuredValueWithinThresholdsCases")
  public void shouldCheckMeasuredValueWithinThresholds(@Nullable final BigDecimal prevValue, @NotNull final BigDecimal measuredValue, @NotNull final ThresholdValue threshold, boolean expectedResult)
  {
    // Given
    final MetricComparer instance = createInstance();

    // When
    final boolean actualResult = instance.isMeasuredValueWithinThresholds(prevValue, measuredValue, threshold);

    // Then

    then(actualResult).isEqualTo(expectedResult);
  }

  @NotNull
  private MetricComparer createInstance()
  {
    return new MetricComparerImpl();
  }
}
