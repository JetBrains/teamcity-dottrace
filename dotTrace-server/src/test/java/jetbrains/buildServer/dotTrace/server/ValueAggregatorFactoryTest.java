package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;
import org.jmock.Mockery;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ValueAggregatorFactoryTest {
  private final Mockery myCtx;
  private final ValueAggregator myValueAggregatorFirst;
  private final ValueAggregator myValueAggregatorLast;
  private final ValueAggregator myValueAggregatorAverage;
  private final ValueAggregator myValueAggregatorSkipped;

  public ValueAggregatorFactoryTest() {
    myCtx = new Mockery();
    myValueAggregatorSkipped = myCtx.mock(ValueAggregator.class, "ValueAggregatorSkipped");
    myValueAggregatorFirst = myCtx.mock(ValueAggregator.class, "ValueAggregatorFirst");
    myValueAggregatorLast = myCtx.mock(ValueAggregator.class, "ValueAggregatorLast");
    myValueAggregatorAverage = myCtx.mock(ValueAggregator.class, "ValueAggregatorAverage");
  }

  @DataProvider(name = "createCases")
  public Object[][] getCreateCases() {
    return new Object[][] {
      { ThresholdValueType.SKIPPED, myValueAggregatorSkipped },
      { ThresholdValueType.FIRST, myValueAggregatorFirst },
      { ThresholdValueType.LAST, myValueAggregatorLast },
      { ThresholdValueType.AVERAGE, myValueAggregatorAverage },
    };
  }

  @Test(dataProvider = "createCases")
  public void shouldCreate(@NotNull final ThresholdValueType type, @NotNull final ValueAggregator expectedValueAggregator)
  {
    // Given
    final ValueAggregatorFactory instance = createInstance();

    // When
    final ValueAggregator actualValueAggregator = instance.create(type);

    // Then
    then(actualValueAggregator).isEqualTo(expectedValueAggregator);
  }

  @NotNull
  private ValueAggregatorFactory createInstance()
  {
    return new ValueAggregatorFactoryImpl(
      myValueAggregatorSkipped,
      myValueAggregatorFirst,
      myValueAggregatorLast,
      myValueAggregatorAverage);
  }
}
