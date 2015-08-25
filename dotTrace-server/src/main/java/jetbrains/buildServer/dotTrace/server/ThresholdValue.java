package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class ThresholdValue {
  private static final Pattern statisticPattern = Pattern.compile("(F|A|L)([0-9,\\.]+)", Pattern.CASE_INSENSITIVE);

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

    final Matcher matcher = statisticPattern.matcher(valueStr);
    if(!matcher.find()) {
      return null;
    }

    if(matcher.groupCount() != 2) {
      return null;
    }

    final ThresholdValueType type = ThresholdValueType.tryParse(matcher.group(1));
    if(type == null) {
      return null;
    }

    try {
      return new ThresholdValue(type, new BigDecimal(matcher.group(2)));
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