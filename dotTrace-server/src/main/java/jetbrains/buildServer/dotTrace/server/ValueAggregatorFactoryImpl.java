package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;

public class ValueAggregatorFactoryImpl implements ValueAggregatorFactory {
  private final ValueAggregator myValueAggregatorSkipped;
  private final ValueAggregator myValueAggregatorFirst;
  private final ValueAggregator myValueAggregatorLast;
  private final ValueAggregator myValueAggregatorAverage;

  public ValueAggregatorFactoryImpl(
    @NotNull final ValueAggregator valueAggregatorSkipped,
    @NotNull final ValueAggregator valueAggregatorFirst,
    @NotNull final ValueAggregator valueAggregatorLast,
    @NotNull final ValueAggregator valueAggregatorAverage) {
    myValueAggregatorSkipped = valueAggregatorSkipped;
    myValueAggregatorFirst = valueAggregatorFirst;
    myValueAggregatorLast = valueAggregatorLast;
    myValueAggregatorAverage = valueAggregatorAverage;
  }

  @NotNull
  @Override
  public ValueAggregator create(@NotNull final ThresholdValueType type) {
    switch (type) {
      case SKIPPED:
        return myValueAggregatorSkipped;

      case FIRST:
        return myValueAggregatorFirst;

      case LAST:
        return myValueAggregatorLast;

      case AVERAGE:
        return myValueAggregatorAverage;

      default:
        throw new UnsupportedOperationException();
    }
  }
}
