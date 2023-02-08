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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class ValueAggregatorFirst implements ValueAggregator {
  private boolean myIsCompleted = false;
  private BigDecimal myVal = null;

  @Override
  public void aggregate(@NotNull final BigDecimal value) {
    if(myIsCompleted) {
      return;
    }

    myIsCompleted = true;
    myVal = value;
  }

  @Override
  public boolean isCompleted() {
    return myIsCompleted;
  }

  @Nullable
  @Override
  public BigDecimal tryGetAggregatedValue() {
    return myVal;
  }
}
