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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;

public class ValueAggregatorFactoryImpl implements ValueAggregatorFactory {
  private final ValueAggregator myValueAggregatorFirst;
  private final ValueAggregator myValueAggregatorLast;
  private final ValueAggregator myValueAggregatorAverage;

  public ValueAggregatorFactoryImpl(
    @NotNull final ValueAggregator valueAggregatorFirst,
    @NotNull final ValueAggregator valueAggregatorLast,
    @NotNull final ValueAggregator valueAggregatorAverage) {
    myValueAggregatorFirst = valueAggregatorFirst;
    myValueAggregatorLast = valueAggregatorLast;
    myValueAggregatorAverage = valueAggregatorAverage;
  }

  @Nullable
  @Override
  public ValueAggregator tryCreate(@NotNull final ThresholdValueType type) {
    switch (type) {
      case FIRST:
        return myValueAggregatorFirst;

      case LAST:
        return myValueAggregatorLast;

      case AVERAGE:
        return myValueAggregatorAverage;

      default:
        return null;
    }
  }
}
