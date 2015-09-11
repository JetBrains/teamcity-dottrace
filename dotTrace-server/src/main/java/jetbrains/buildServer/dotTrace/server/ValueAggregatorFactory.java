package jetbrains.buildServer.dotTrace.server;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface ValueAggregatorFactory {
  @Nullable
  ValueAggregator tryCreate(@NotNull final ThresholdValueType type);
}
