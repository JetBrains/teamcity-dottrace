package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.Nullable;

public enum ThresholdValueType {
  SKIPPED,
  ABSOLUTE,
  FIRST,
  LAST,
  AVERAGE;

  @Nullable
  public static ThresholdValueType tryParse(@Nullable final String valueStr) {
    if("L".equalsIgnoreCase(valueStr)) {
      return LAST;
    }

    if("A".equalsIgnoreCase(valueStr)) {
      return AVERAGE;
    }

    if("F".equalsIgnoreCase(valueStr)) {
      return FIRST;
    }

    return null;
  }
}
