/*
 * Copyright 2000-2021 JetBrains s.r.o.
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import jetbrains.buildServer.dotNet.buildRunner.agent.XmlDocumentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReportParser implements TextParser<Metrics> {
  private static final String ERROR_DURING_PARSING_ERROR_MESSAGE = "Error during parsing dotTrace report xml document";
  private static final String FUNCTION_XPATH = "//Report/Function";
  private static final String METHOD_NAME_ATTR = "FQN";
  private static final String TOTAL_TIME_ATTR = "TotalTime";
  private static final String OWN_TIME_ATTR = "OwnTime";
  private final XmlDocumentManager myXmlDocumentManager;

  public ReportParser(
    @NotNull final XmlDocumentManager xmlDocumentManager) {
    myXmlDocumentManager = xmlDocumentManager;
  }

  @NotNull
  @Override
  public Metrics parse(@NotNull final String reportContent) {
    if(StringUtil.isEmptyOrSpaces(reportContent)) {
      return new Metrics(Collections.<Metric>emptyList());
    }

    final List<Metric> metrics = new ArrayList<Metric>();
    final Document doc = myXmlDocumentManager.convertStringToDocument(reportContent);
    final XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      final NodeList functionElements = (NodeList)xpath.evaluate(FUNCTION_XPATH, doc, XPathConstants.NODESET);
      for (int functionIndex = 0; functionIndex < functionElements.getLength(); functionIndex++) {
        final Node functionElement = functionElements.item(functionIndex);
        @Nullable final Node methodNameAttr = functionElement.getAttributes().getNamedItem(METHOD_NAME_ATTR);
        @Nullable final Node totalTimeAttr = functionElement.getAttributes().getNamedItem(TOTAL_TIME_ATTR);
        @Nullable final Node ownTimeAttr = functionElement.getAttributes().getNamedItem(OWN_TIME_ATTR);
        if(methodNameAttr == null || totalTimeAttr == null || ownTimeAttr == null) {
          continue;
        }

        final String methodName = methodNameAttr.getNodeValue();
        final String totalTime = totalTimeAttr.getNodeValue();
        final String ownTime = ownTimeAttr.getNodeValue();

        if(StringUtil.isEmptyOrSpaces(methodName) || StringUtil.isEmptyOrSpaces(totalTime) || StringUtil.isEmptyOrSpaces(ownTime)) {
          continue;
        }

        metrics.add(new Metric(methodName, totalTime, ownTime));
      }
    }
    catch (XPathExpressionException e) {
      throw new BuildException(ERROR_DURING_PARSING_ERROR_MESSAGE);
    }

    return new Metrics(metrics);
  }
}
