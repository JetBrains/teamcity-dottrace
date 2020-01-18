/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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