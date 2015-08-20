package jetbrains.buildServer.dotTrace;

import com.sun.istack.internal.NotNull;

public enum MeasureType {
  SAMPLING("sampling", "Sampling", "Sampling"),
  TRACING("tracing", "Tracing", ""),
  LINE_BY_LINE("line-by-line", "Line-by-line", "TracingInject");

  private final String myValue;
  private final String myDescription;
  private final String myId;

  MeasureType(@NotNull final String value, @NotNull final String description, @NotNull final String id) {
    myValue = value;
    myDescription = description;
    myId = id;
  }

  @NotNull
  public String getValue() {
    return myValue;
  }

  @NotNull
  public String getDescription() {
    return myDescription;
  }

  @NotNull
  @Override
  public String toString() {
    return myDescription;
  }

  @NotNull
  public String getId() {
    return myId;
  }
}