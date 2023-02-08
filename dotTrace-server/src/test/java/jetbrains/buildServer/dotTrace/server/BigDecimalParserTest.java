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

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BigDecimalParserTest {
  @DataProvider(name = "parseValueFromTextCases")
  public Object[][] getParseValueFromTextCases() {
    return new Object[][] {
      { "50", new BigDecimal(50)},
      { "50.1", new BigDecimal("50.1")},
      { null, null},
      { "", null},
      { " ", null},
      { "aa", null},
      { "aa50", null},
    };
  }

  @Test(dataProvider = "parseValueFromTextCases")
  public void shouldParseValueFromText(@Nullable final String valueStr, @Nullable BigDecimal expectedValue) {
    // Given

    // When
    BigDecimalParser instance = createInstance();
    final BigDecimal actualValue = instance.tryParseBigDecimal(valueStr);

    // Then
    then(actualValue).isEqualTo(expectedValue);
  }


  @NotNull
  private BigDecimalParser createInstance()
  {
    return new BigDecimalParserImpl();
  }
}
