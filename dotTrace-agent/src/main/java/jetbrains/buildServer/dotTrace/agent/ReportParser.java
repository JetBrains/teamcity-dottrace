package jetbrains.buildServer.dotTrace.agent;

import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;

public class ReportParser implements TextParser<Thresholds> {
  @NotNull
  @Override
  public Thresholds parse(final String s) {
    return new Thresholds(Collections.<Threshold>emptyList());
  }
}
