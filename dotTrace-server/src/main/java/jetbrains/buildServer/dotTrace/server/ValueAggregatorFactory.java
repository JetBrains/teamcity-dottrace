package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;

public interface ValueAggregatorFactory {
  @NotNull ValueAggregator create(@NotNull final ThresholdValueType type);
}
