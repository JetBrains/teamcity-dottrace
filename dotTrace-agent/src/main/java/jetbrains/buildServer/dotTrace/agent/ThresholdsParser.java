package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.util.ArrayList;
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;

public class ThresholdsParser implements TextParser<Thresholds> {
  private static final String ARG_SEPARATOR = " ";
  private static final String ourlineSeparator = System.getProperty("line.separator");

  @NotNull
  @Override
  public Thresholds parse(@NotNull final String text) {
    final List<String> lines = StringUtil.split(text, ourlineSeparator);
    List<Threshold> thresholds = new ArrayList<Threshold>(lines.size());
    for(String line: lines) {
      if(StringUtil.isEmptyOrSpaces(line)) {
        continue;
      }

      final String[] params = line.split(ARG_SEPARATOR);
      if(params.length != 3) {
        throw new BuildException("Invalid thresholds");
      }

      thresholds.add(new Threshold(params[0], params[1], params[2]));
    }

    return new Thresholds(thresholds);
  }
}
