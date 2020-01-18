/*
 * Copyright 2000-2020 JetBrains s.r.o.
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
