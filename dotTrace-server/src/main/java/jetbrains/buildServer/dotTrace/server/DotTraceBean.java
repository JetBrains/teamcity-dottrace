

package jetbrains.buildServer.dotTrace.server;

import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.dotTrace.MeasureType;
import org.jetbrains.annotations.NotNull;

public class DotTraceBean {
  public static final DotTraceBean Shared = new DotTraceBean();

  @NotNull
  public String getUseDotTraceKey() {
    return Constants.USE_VAR;
  }

  @NotNull
  public String getPathKey() {
    return Constants.PATH_VAR;
  }

  @NotNull
  public String getThresholdsKey() {
    return Constants.THRESHOLDS_VAR;
  }

  @NotNull
  public String getMeasureTypeKey() {
    return Constants.MEASURE_TYPE_VAR;
  }

  @NotNull
  public String getProfileChildProcessesKey() {
    return Constants.PROFILE_CHILD_PROCESSES_VAR;
  }

  @NotNull
  public String getProcessFiltersKey() {
    return Constants.PROCESS_FILTERS_VAR;
  }

  @NotNull
  public String getSnapshotsPathKey() {
    return Constants.SNAPSHOTS_PATH_VAR;
  }

  @NotNull
  public MeasureType[] getMeasureTypes() {
    return MeasureType.values();
  }
}