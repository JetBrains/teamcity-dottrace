package jetbrains.buildServer.dotTrace.agent;

import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;

public class ReportParser implements TextParser<Metrics> {
  @NotNull
  @Override
  public Metrics parse(@NotNull final String reportContent) {
    return new Metrics(Collections.<Metric>emptyList());
  }
}
