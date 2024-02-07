

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

public interface BigDecimalParser {
  @Nullable BigDecimal tryParseBigDecimal(@Nullable final String valueStr);
}