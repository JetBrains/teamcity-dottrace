/*
 * Copyright 2000-2023 JetBrains s.r.o.
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