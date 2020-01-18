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

import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import jetbrains.buildServer.dotNet.buildRunner.agent.XmlDocumentManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ReportParserTest {
  @DataProvider(name = "parseDotTraceReportFromXmlCases")
  public Object[][] getParseDotTraceReportFromXmlCases() {
    return new Object[][] {
      {
        ourXmlSample,
        new Metrics(
          Arrays.asList(
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM01", "26", "10"),
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM02", "334", "0"),
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM03", "100", "0")))},
      {
        ourXmlSample2,
        new Metrics(
          Arrays.asList(
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM01", "26", "10"),
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM02", "334", "0"),
            new Metric("HelloWorld.Tests.UnitTest4.TestMethodCatM03", "100", "0")))},
      {
        "",
        new Metrics(Collections.<Metric>emptyList())},
    };
  }

  @Test(dataProvider = "parseDotTraceReportFromXmlCases")
  public void shouldParseDotTraceReportFromXml(@NotNull final String text, @NotNull final Metrics expectedMetrics)
  {
    final TextParser<Metrics> instance = createInstance();

    // When
    final Metrics actualMetrics = instance.parse(text);

    // Then
    then(actualMetrics).isEqualTo(expectedMetrics);
  }

  @NotNull
  private TextParser<Metrics> createInstance()
  {
    return new ReportParser(new XmlDocumentManagerImpl());
  }

  private static final String ourXmlSample =
    "<Report>\n" +
    "<Function Id=\"0x01200007\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM01\" TotalTime=\"26\" OwnTime=\"10\" Calls=\"4\" Instances=\"1\"/>\n" +
    "<Function Id=\"0x01200008\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM02\" TotalTime=\"334\" OwnTime=\"0\" Calls=\"1\" Instances=\"10\"/>\n" +
    "<Function Id=\"0x01200009\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM03\" TotalTime=\"100\" OwnTime=\"0\" Calls=\"18\" Instances=\"1\"/>\n" +
    "</Report>";

  private static final String ourXmlSample2 =
    "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
    "<Report>\n" +
    "<Function Id=\"0x01200007\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM01\" TotalTime=\"26\" OwnTime=\"10\" Calls=\"4\" Instances=\"1\"/>\n" +
    "<Function Id=\"0x01200008\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM02\" TotalTime=\"334\" OwnTime=\"0\" Calls=\"1\" Instances=\"10\"/>\n" +
    "<Function Id=\"0x01200009\" FQN=\"HelloWorld.Tests.UnitTest4.TestMethodCatM03\" TotalTime=\"100\" OwnTime=\"0\" Calls=\"18\" Instances=\"1\"/>\n" +
    "</Report>";
}
