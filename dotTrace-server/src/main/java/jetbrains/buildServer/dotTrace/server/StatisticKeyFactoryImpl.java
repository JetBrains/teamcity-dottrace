/*
 * Copyright 2000-2023 JetBrains s.r.o.
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

import org.jetbrains.annotations.NotNull;

public class StatisticKeyFactoryImpl implements StatisticKeyFactory {
  public static final String DOT_TRACE_TOTAL_TIME_STATISTIC_KEY = "dot_trace_total_time";
  public static final String DOT_TRACE_OWN_TIME_STATISTIC_KEY = "dot_trace_own_time";

  @NotNull
  @Override
  public String createTotalTimeKey(@NotNull final String methodName) {
    return methodName + ":" + DOT_TRACE_TOTAL_TIME_STATISTIC_KEY;
  }

  @NotNull
  @Override
  public String createOwnTimeKey(@NotNull final String methodName) {
    return methodName + ":" + DOT_TRACE_OWN_TIME_STATISTIC_KEY;
  }
}
