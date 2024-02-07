

package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public interface ValueAggregatorFactory {
  @Nullable
  ValueAggregator tryCreate(@NotNull final ThresholdValueType type);
}