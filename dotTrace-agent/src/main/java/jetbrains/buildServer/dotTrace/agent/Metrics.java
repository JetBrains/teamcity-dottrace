/*
 * Copyright 2000-2021 JetBrains s.r.o.
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

package jetbrains.buildServer.dotTrace.agent;

import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Metrics {
  private final List<Metric> myMetrics;

  public Metrics(@NotNull final List<Metric> metrics) {
    myMetrics = Collections.unmodifiableList(metrics);
  }

  @NotNull
  public List<Metric> getMetrics() {
    return myMetrics;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Metrics metrics = (Metrics)o;

    return getMetrics().equals(metrics.getMetrics());

  }

  @Override
  public int hashCode() {
    return getMetrics().hashCode();
  }
}
