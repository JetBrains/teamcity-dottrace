

package jetbrains.buildServer.dotTrace.server;

import com.intellij.openapi.util.text.StringUtil;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

public class BigDecimalParserImpl implements BigDecimalParser {
  @Nullable
  @Override
  public BigDecimal tryParseBigDecimal(@Nullable final String valueStr) {
    if(StringUtil.isEmptyOrSpaces(valueStr)) {
      return null;
    }

    try {
      return new BigDecimal(valueStr);
    }
    catch (NumberFormatException ignored) {
      return null;
    }
  }
}