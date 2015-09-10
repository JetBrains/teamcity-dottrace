package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public interface HistoryElement {
  @Nullable BigDecimal tryGetValue(@NotNull final String key);
}
