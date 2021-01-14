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

package jetbrains.buildServer.dotTrace.agent;

import org.jetbrains.annotations.NotNull;

public class ProcessFilter {
  private final String myMask;

  public ProcessFilter(@NotNull final String mask) {
    myMask = mask;
  }

  @NotNull
  public String getMask() {
    return myMask;
  }

  @Override
  public String toString() {
    return "ProcessFilter{" +
           "Mask='" + myMask + '\'' +
           '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ProcessFilter that = (ProcessFilter)o;

    return getMask().equals(that.getMask());

  }

  @Override
  public int hashCode() {
    return getMask().hashCode();
  }
}
