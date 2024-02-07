

package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.util.ArrayList;
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;

public class ThresholdsParser implements TextParser<Metrics> {
  private static final String ARG_SEPARATOR = " ";
  private static final String ourlineSeparator = "\n";

  @NotNull
  @Override
  public Metrics parse(@NotNull final String text) {
    final List<String> lines = StringUtil.split(text, ourlineSeparator);
    List<Metric> metrics = new ArrayList<Metric>(lines.size());
    for(String line: lines) {
      if(StringUtil.isEmptyOrSpaces(line)) {
        continue;
      }

      final String[] params = line.trim().split(ARG_SEPARATOR);
      if(params.length != 3) {
        throw new BuildException("Invalid metrics");
      }

      metrics.add(new Metric(params[0], params[1], params[2]));
    }

    return new Metrics(metrics);
  }
}