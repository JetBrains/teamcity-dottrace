package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;

public interface BigDecimalParser {
  @Nullable BigDecimal tryParseBigDecimal(@Nullable final String valueStr);
}
