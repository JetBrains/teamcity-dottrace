

package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;

public class ProcessFilterParser implements TextParser<List<ProcessFilter>> {
  private static final String ourlineSeparator = "\n";

  @NotNull
  @Override
  public List<ProcessFilter> parse(@NotNull final String filtersText) {
    final List<ProcessFilter> filters = new ArrayList<ProcessFilter>();
    for(String mask: StringUtil.split(filtersText, ourlineSeparator)) {
      if(StringUtil.isEmptyOrSpaces(mask)) {
        continue;
      }

      filters.add(new ProcessFilter(mask));
    }

    return Collections.unmodifiableList(filters);
  }
}