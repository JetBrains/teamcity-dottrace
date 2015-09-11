package jetbrains.buildServer.dotTrace.server;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jmock.Mockery;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ValueAggregatorFactoryTest {
  private final ValueAggregator myValueAggregatorFirst;
  private final ValueAggregator myValueAggregatorLast;
  private final ValueAggregator myValueAggregatorAverage;

  public ValueAggregatorFactoryTest() {
    final Mockery ctx = new Mockery();
    myValueAggregatorFirst = ctx.mock(ValueAggregator.class, "ValueAggregatorFirst");
    myValueAggregatorLast = ctx.mock(ValueAggregator.class, "ValueAggregatorLast");
    myValueAggregatorAverage = ctx.mock(ValueAggregator.class, "ValueAggregatorAverage");
  }

  @DataProvider(name = "createCases")
  public Object[][] getCreateCases() {
    return new Object[][] {
      { ThresholdValueType.FIRST, myValueAggregatorFirst },
      { ThresholdValueType.LAST, myValueAggregatorLast },
      { ThresholdValueType.AVERAGE, myValueAggregatorAverage },
      { ThresholdValueType.SKIPPED, null },
      { ThresholdValueType.ABSOLUTE, null },
    };
  }

  @Test(dataProvider = "createCases")
  public void shouldCreate(@NotNull final ThresholdValueType type, @Nullable final ValueAggregator expectedValueAggregator)
  {
    // Given
    final ValueAggregatorFactory instance = createInstance();

    // When
    final ValueAggregator actualValueAggregator = instance.tryCreate(type);

    // Then
    then(actualValueAggregator).isEqualTo(expectedValueAggregator);
  }

  @NotNull
  private ValueAggregatorFactory createInstance()
  {
    return new ValueAggregatorFactoryImpl(
      myValueAggregatorFirst,
      myValueAggregatorLast,
      myValueAggregatorAverage);
  }
}
