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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class StatisticKeyFactoryTest {
  @DataProvider(name = "createOwnTimeKeyCases")
  public Object[][] getCreateOwnTimeKeyCases() {
    return new Object[][] {
      { "method1", "method1:" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { "method1:aaa", "method1:aaa:" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { "", ":" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { " ", " :" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY}
    };
  }

  @Test(dataProvider = "createOwnTimeKeyCases")
  public void shouldCreateOwnTimeKey(@NotNull final String methodName, @Nullable String expectedKey) {
    // Given

    // When
    StatisticKeyFactory instance = createInstance();
    final String actualKey = instance.createOwnTimeKey(methodName);

    // Then
    then(actualKey).isEqualTo(expectedKey);
  }

  @DataProvider(name = "createTotalTimeKeyCases")
  public Object[][] getCreateTotalTimeKeyCases() {
    return new Object[][] {
      { "method1", "method1:" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { "method1:aaa", "method1:aaa:" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { "", ":" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { " ", " :" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY}
    };
  }

  @Test(dataProvider = "createTotalTimeKeyCases")
  public void shouldCreateTotalTimeKey(@NotNull final String methodName, @Nullable String expectedKey) {
    // Given

    // When
    StatisticKeyFactory instance = createInstance();
    final String actualKey = instance.createTotalTimeKey(methodName);

    // Then
    then(actualKey).isEqualTo(expectedKey);
  }

  @NotNull
  private StatisticKeyFactory createInstance()
  {
    return new StatisticKeyFactoryImpl();
  }
}
