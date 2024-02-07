

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public interface HistoryElement {
  @Nullable BigDecimal tryGetValue(@NotNull final String key);
}