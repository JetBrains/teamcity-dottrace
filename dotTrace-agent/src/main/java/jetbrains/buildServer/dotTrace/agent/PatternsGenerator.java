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
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PatternsGenerator implements ResourceGenerator<Context> {
  private static final String PATTERNS_ELEMENT = "Patterns";
  private static final String PATTERN_ELEMENT = "Pattern";

  private final TextParser<Metrics> myThresholdsParser;
  private final RunnerParametersService myParametersService;
  private final XmlDocumentManager myDocumentManager;

  public PatternsGenerator(
    @NotNull final TextParser<Metrics> thresholdsParser,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final XmlDocumentManager documentManager) {
    myThresholdsParser = thresholdsParser;
    myParametersService = parametersService;
    myDocumentManager = documentManager;
  }

  @NotNull
  @Override
  public String create(@NotNull final Context context) {
    final Document doc = myDocumentManager.createDocument();
    final Element patternsElement = doc.createElement(PATTERNS_ELEMENT);
    String thresholdsStr = myParametersService.tryGetRunnerParameter(Constants.THRESHOLDS_VAR);
    if(!StringUtil.isEmptyOrSpaces(thresholdsStr)) {
      final Metrics metrics = myThresholdsParser.parse(thresholdsStr);
      for(Metric metric : metrics.getMetrics()) {
        final Element patternElement = createSimpleElement(doc, PATTERN_ELEMENT, metric.getMethodName());
        patternsElement.appendChild(patternElement);
      }
    }

    doc.appendChild(patternsElement);
    return myDocumentManager.convertDocumentToString(doc, Collections.<String, String>emptyMap());
  }

  @NotNull
  private static Element createSimpleElement(@NotNull final Document doc, @NotNull final String name, @NotNull final String value) {
    Element executableElement = doc.createElement(name);
    executableElement.setTextContent(value);
    return executableElement;
  }
}
