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

import com.intellij.util.containers.SortedList;
import java.math.BigDecimal;
import java.util.*;
import org.jetbrains.annotations.Nullable;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import org.jetbrains.annotations.NotNull;

public class HistoryImpl implements History {
  private final BuildDataStorage myStorage;

  public HistoryImpl(
    @NotNull final BuildDataStorage storage) {
    myStorage = storage;
  }

  @Override
  @NotNull
  public Iterable<HistoryElement> getElements(@NotNull final List<SFinishedBuild> builds) {
    final SortedList<SFinishedBuild> sortedBuilds = new SortedList<SFinishedBuild>(new Comparator<SFinishedBuild>() {
      @Override
      public int compare(final SFinishedBuild o1, final SFinishedBuild o2) {
        return o1.getFinishDate().compareTo(o2.getFinishDate());
      }
    });

    sortedBuilds.addAll(builds);

    return new Iterable<HistoryElement>() {
      @Override
      public Iterator<HistoryElement> iterator() {
        return new Iterator<HistoryElement>() {
          private int index = 0;

          @Override
          public boolean hasNext() {
            return index < sortedBuilds.size();
          }

          @Override
          public HistoryElement next() {
            final int curIndex = index++;
            return new HistoryElement() {
              @Nullable
              @Override
              public BigDecimal tryGetValue(@NotNull final String key) {
                return myStorage.getValues(sortedBuilds.get(curIndex)).get(key);
              }
            };
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}
