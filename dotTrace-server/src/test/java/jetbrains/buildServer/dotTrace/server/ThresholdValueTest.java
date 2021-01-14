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

package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ThresholdValueTest {
  @DataProvider(name = "parseValueFromTextCases")
  public Object[][] getParseValueFromTextCases() {
    return new Object[][] {
      { "L50", new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(50))},
      { "F50", new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(50))},
      { "A50", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(50))},
      { "A100", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(100))},
      { "A0", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(0))},
      { "A200", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(200))},
      { "A", null},
      { "F", null},
      { "L", null},
      { "50", new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(50))},
      { " 50", new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(50))},
      { "50 ", new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(50))},
      { " 50 ", new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(50))},
      { "A-50", null},
      { "-50", null},
      { null, null},
      { "", null},
      { "  ", null},
      { "Z", null},
      { "@", null},
      { "0", new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(0))},
      { " 0", new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(0))},
      { "0 ", new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(0))},
      { " 0 ", new ThresholdValue(ThresholdValueType.SKIPPED, new BigDecimal(0))},
    };
  }

  @Test(dataProvider = "parseValueFromTextCases")
  public void shouldParseValueFromText(@Nullable final String valueStr, @Nullable ThresholdValue expectedValue) {
    // Given

    // When
    final ThresholdValue actualValue = ThresholdValue.tryParse(valueStr);

    // Then
    then(actualValue).isEqualTo(expectedValue);
  }
}
