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

package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jmock.Mockery;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ValueAggregatorFactoryTest {
  private final ValueAggregator myValueAggregatorFirst;
  private final ValueAggregator myValueAggregatorLast;
  private final ValueAggregator myValueAggregatorAverage;

  public ValueAggregatorFactoryTest() {
    final Mockery ctx = new Mockery();
    myValueAggregatorFirst = ctx.mock(ValueAggregator.class, "ValueAggregatorFirst");
    myValueAggregatorLast = ctx.mock(ValueAggregator.class, "ValueAggregatorLast");
    myValueAggregatorAverage = ctx.mock(ValueAggregator.class, "ValueAggregatorAverage");
  }

  @DataProvider(name = "createCases")
  public Object[][] getCreateCases() {
    return new Object[][] {
      { ThresholdValueType.FIRST, myValueAggregatorFirst },
      { ThresholdValueType.LAST, myValueAggregatorLast },
      { ThresholdValueType.AVERAGE, myValueAggregatorAverage },
      { ThresholdValueType.SKIPPED, null },
      { ThresholdValueType.ABSOLUTE, null },
    };
  }

  @Test(dataProvider = "createCases")
  public void shouldCreate(@NotNull final ThresholdValueType type, @Nullable final ValueAggregator expectedValueAggregator)
  {
    // Given
    final ValueAggregatorFactory instance = createInstance();

    // When
    final ValueAggregator actualValueAggregator = instance.tryCreate(type);

    // Then
    then(actualValueAggregator).isEqualTo(expectedValueAggregator);
  }

  @NotNull
  private ValueAggregatorFactory createInstance()
  {
    return new ValueAggregatorFactoryImpl(
      myValueAggregatorFirst,
      myValueAggregatorLast,
      myValueAggregatorAverage);
  }
}
