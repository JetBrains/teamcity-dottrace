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
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class MetricsParserTest {
  private static final String ourlineSeparator = "\n";

  @DataProvider(name = "parseThresholdsFromStringCases")
  public Object[][] getParseThresholdsFromStringCases() {
    return new Object[][] {
      { "IntegrationTests.MainTests.Test1 100 F15", new Metrics(Arrays.asList(new Metric("IntegrationTests.MainTests.Test1", "100", "F15"))), false },
      { "IntegrationTests.MainTests.Test1 100 F15" + ourlineSeparator + "IntegrationTests.MainTests.Test2 200 A15", new Metrics(Arrays.asList(new Metric("IntegrationTests.MainTests.Test1", "100", "F15"), new Metric("IntegrationTests.MainTests.Test2", "200", "A15"))), false },
      { "   " + ourlineSeparator + "IntegrationTests.MainTests.Test1 100 F15" + ourlineSeparator + "   " + ourlineSeparator + ourlineSeparator + "IntegrationTests.MainTests.Test2 200 A15" + ourlineSeparator, new Metrics(Arrays.asList(new Metric("IntegrationTests.MainTests.Test1", "100", "F15"), new Metric("IntegrationTests.MainTests.Test2", "200", "A15"))), false },
      { "", new Metrics(Collections.<Metric>emptyList()), false },
      { "IntegrationTests.MainTests.Test1 100 F15 300", new Metrics(Collections.<Metric>emptyList()), true },
      { "IntegrationTests.MainTests.Test1 100", new Metrics(Collections.<Metric>emptyList()), true },
      { "IntegrationTests.MainTests.Test1", new Metrics(Collections.<Metric>emptyList()), true },
      { "IntegrationTests.MainTests.Test1" + ourlineSeparator + "IntegrationTests.MainTests.Test2 200 A15", new Metrics(Collections.<Metric>emptyList()), true },
    };
  }

  @Test(dataProvider = "parseThresholdsFromStringCases")
  public void shouldParseThresholdsFromString(@NotNull final String text, @NotNull final Metrics expectedMetrics, boolean expectedExceptionThrown)
  {
    // Given
    final TextParser<Metrics> instance = createInstance();

    // When
    Metrics actualMetrics = null;
    boolean actualExceptionThrown = false;

    try {
      actualMetrics = instance.parse(text);
    }
    catch (BuildException ex) {
      actualExceptionThrown = true;
    }

    // Then
    if(!expectedExceptionThrown) {
      //noinspection ConstantConditions,ConstantConditions
      then(actualMetrics.getMetrics()).containsExactlyElementsOf(expectedMetrics.getMetrics());
    }

    then(actualExceptionThrown).isEqualTo(expectedExceptionThrown);
  }

  @NotNull
  private TextParser<Metrics> createInstance()
  {
    return new ThresholdsParser();
  }
}