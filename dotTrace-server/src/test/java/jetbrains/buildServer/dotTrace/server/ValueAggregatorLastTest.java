

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ValueAggregatorLastTest {
  @DataProvider(name = "aggregateCases")
  public Object[][] getAggregateCases() {
    return new Object[][] {
      { Arrays.asList(new BigDecimal(33)), false, new BigDecimal(33) },
      { Arrays.asList(), false, null },
      { Arrays.asList(new BigDecimal(33), new BigDecimal(44), new BigDecimal(55)), false, new BigDecimal(55) },
      { Arrays.asList(new BigDecimal(33), new BigDecimal(99), new BigDecimal(44), new BigDecimal(33), new BigDecimal(99)), false, new BigDecimal(99) },
    };
  }

  @Test(dataProvider = "aggregateCases")
  public void shouldAggregate(@NotNull final Iterable<BigDecimal> values, final boolean expectedIsCompleted, @Nullable final BigDecimal expectedAggregatedValue)
  {
    // Given
    final ValueAggregator instance = createInstance();

    // When
    for(BigDecimal value: values) {
      instance.aggregate(value);
    }

    // Then
    then(instance.isCompleted()).isEqualTo(expectedIsCompleted);
    then(instance.tryGetAggregatedValue()).isEqualTo(expectedAggregatedValue);
  }

  @NotNull
  private ValueAggregator createInstance()
  {
    return new ValueAggregatorLast();
  }
}