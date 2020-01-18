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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ValueAggregatorAverage implements ValueAggregator {
  private final List<BigDecimal> myVals = new ArrayList<BigDecimal>();

  @Override
  public void aggregate(@NotNull final BigDecimal value) {
    myVals.add(value);
  }

  @Override
  public boolean isCompleted() {
    return false;
  }

  @Nullable
  @Override
  public BigDecimal tryGetAggregatedValue() {
    final int size = myVals.size();
    if(size == 0) {
      return null;
    }

    if(size == 1) {
      return myVals.get(0);
    }

    BigDecimal sum = myVals.get(0);
    for(int i=1; i<size; i++) {
      sum = sum.add(myVals.get(i));
    }

    return sum.divide(new BigDecimal(size), 4, RoundingMode.HALF_UP);
  }
}
