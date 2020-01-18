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

import java.math.BigDecimal;
import java.util.Arrays;
import org.assertj.core.data.Offset;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ValueAggregatorAverageTest {
  @DataProvider(name = "aggregateCases")
  public Object[][] getAggregateCases() {
    return new Object[][] {
      { Arrays.asList(new BigDecimal(33)), false, new BigDecimal(33) },
      { Arrays.asList(), false, null },
      { Arrays.asList(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)), false, new BigDecimal(2) },
      { Arrays.asList(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(2), new BigDecimal(7)), false, new BigDecimal(3) },
      { Arrays.asList(new BigDecimal(1), new BigDecimal(2), new BigDecimal(2)), false, new BigDecimal(1.6667) },
    };
  }

  @Test(dataProvider = "aggregateCases")
  public void shouldAggregate(@NotNull final Iterable<BigDecimal> values, final boolean expectedIsCompleted, @Nullable final BigDecimal expectedAggregatedValue)
  {
    // Given
    final ValueAggregator instance = createInstance();

    // When
    for(BigDecimal value: values) {
      instance.aggregate(value);
    }

    // Then
    then(instance.isCompleted()).isEqualTo(expectedIsCompleted);
    if(expectedAggregatedValue == null) {
      then(instance.tryGetAggregatedValue()).isNull();
    }
    else {
      then(instance.tryGetAggregatedValue()).isCloseTo(expectedAggregatedValue, Offset.offset(new BigDecimal(.0001)));
    }
  }

  @NotNull
  private ValueAggregator createInstance()
  {
    return new ValueAggregatorAverage();
  }
}
