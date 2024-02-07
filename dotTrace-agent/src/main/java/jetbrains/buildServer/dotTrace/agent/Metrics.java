

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