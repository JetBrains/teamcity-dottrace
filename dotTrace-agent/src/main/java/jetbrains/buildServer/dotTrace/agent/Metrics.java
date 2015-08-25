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
}