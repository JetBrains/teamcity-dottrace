package jetbrains.buildServer.dotTrace.agent;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Thresholds {
  private final List<Threshold> myThresholds;

  public Thresholds(@NotNull final List<Threshold> thresholds) {
    myThresholds = thresholds;
  }

  @NotNull
  public List<Threshold> getThresholds() {
    return myThresholds;
  }
}
