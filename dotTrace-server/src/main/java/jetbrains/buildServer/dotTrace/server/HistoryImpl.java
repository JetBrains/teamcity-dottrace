package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
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
    return new Iterable<HistoryElement>() {
      @Override
      public Iterator<HistoryElement> iterator() {
        return new Iterator<HistoryElement>() {
          private int index = 0;

          @Override
          public boolean hasNext() {
            return index < builds.size();
          }

          @Override
          public HistoryElement next() {
            final int curIndex = index++;
            return new HistoryElement() {
              @Nullable
              @Override
              public BigDecimal tryGetValue(@NotNull final String key) {
                return myStorage.getValues(builds.get(curIndex)).get(key);
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
