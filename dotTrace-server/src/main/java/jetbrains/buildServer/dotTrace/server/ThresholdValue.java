

package jetbrains.buildServer.dotTrace.server;

import com.intellij.openapi.util.io.StreamUtil;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ThresholdValue {
  private static final Pattern statisticPattern = Pattern.compile("(\\A(F|A|L)([0-9,\\.]+)\\Z)|(\\A[0-9,\\.]+\\Z)", Pattern.CASE_INSENSITIVE);
  private static final ThresholdValue SKIPPED_THRESHOLD_VALUE = new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(0));
  private static final BigDecimal SKIPPED_DECIMAL_VALUE = new BigDecimal("0");

  private final ThresholdValueType myType;
  private final BigDecimal myValue;

  ThresholdValue(@NotNull final ThresholdValueType type, @NotNull final BigDecimal value) {
    myType = type;
    myValue = value;
  }

  @Nullable
  public static ThresholdValue tryParse(@Nullable final String valueStr) {
    if(valueStr == null) {
      return null;
    }

    final Matcher matcher = statisticPattern.matcher(valueStr.trim());
    if(!matcher.find()) {
      return null;
    }

    if(matcher.groupCount() != 4) {
      return null;
    }

    @Nullable
    final String absoluteValStr = matcher.group(4);
    if(absoluteValStr != null) {
      final BigDecimal decimalVal = new BigDecimal(absoluteValStr);
      if(SKIPPED_DECIMAL_VALUE.equals(decimalVal)) {
        return SKIPPED_THRESHOLD_VALUE;
      }

      return new ThresholdValue(ThresholdValueType.ABSOLUTE, decimalVal);
    }

    final ThresholdValueType type = ThresholdValueType.tryParse(matcher.group(2));
    if(type == null) {
      return null;
    }

    try {
      return new ThresholdValue(type, new BigDecimal(matcher.group(3)));
    }
    catch (NumberFormatException ignored) {
    }

    return null;
  }

  public ThresholdValueType getType() {
    return myType;
  }

  public BigDecimal getValue() {
    return myValue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ThresholdValue that = (ThresholdValue)o;

    if (getType() != that.getType()) return false;
    return getValue().equals(that.getValue());

  }

  @Override
  public int hashCode() {
    int result = getType().hashCode();
    result = 31 * result + getValue().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "ThresholdValue{" +
           "Type=" + myType +
           ", Value=" + myValue +
           '}';
  }

}