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
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ProcessFilterParserTest {
  private static final String ourlineSeparator = "\n";

  @DataProvider(name = "parseFiltersFromStringCases")
  public Object[][] getParseFiltersFromStringCases() {
    return new Object[][] {
      { "*service", Arrays.asList(new ProcessFilter("*service")), false },
      { "", Arrays.asList(), false },
      { "*service" + ourlineSeparator + "Abc*.exe", Arrays.asList(new ProcessFilter("*service"), new ProcessFilter("Abc*.exe")), false },
      { "   " + ourlineSeparator + "*service" + ourlineSeparator + ourlineSeparator + "   " + ourlineSeparator + "Abc*.exe" + ourlineSeparator, Arrays.asList(new ProcessFilter("*service"), new ProcessFilter("Abc*.exe")), false },
    };
  }

  @Test(dataProvider = "parseFiltersFromStringCases")
  public void shouldParseFiltersFromString(@NotNull final String text, @NotNull final List<ProcessFilter> expectedFilters, boolean expectedExceptionThrown)
  {
    // Given
    final TextParser<List<ProcessFilter>> instance = createInstance();

    // When
    List<ProcessFilter> actualFilters = null;
    boolean actualExceptionThrown = false;

    try {
      actualFilters = instance.parse(text);
    }
    catch (BuildException ex) {
      actualExceptionThrown = true;
    }

    // Then
    if(!expectedExceptionThrown) {
      //noinspection ConstantConditions,ConstantConditions
      then(actualFilters).containsExactlyElementsOf(expectedFilters);
    }

    then(actualExceptionThrown).isEqualTo(expectedExceptionThrown);
  }

  @NotNull
  private TextParser<List<ProcessFilter>> createInstance()
  {
    return new ProcessFilterParser();
  }
}
