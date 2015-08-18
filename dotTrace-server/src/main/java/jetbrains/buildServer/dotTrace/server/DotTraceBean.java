package jetbrains.buildServer.dotTrace.server;

import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;

public class DotTraceBean {
  public static final DotTraceBean Shared = new DotTraceBean();

  @NotNull
  public String getUseDotTraceKey() {
    return Constants.USE_VAR;
  }

  @NotNull
  public String getDotTracePathKey() {
    return Constants.PATH_VAR;
  }

  @NotNull
  public String getDotTraceThresholdsKey() {
    return Constants.THRESHOLDS_VAR;
  }
}
