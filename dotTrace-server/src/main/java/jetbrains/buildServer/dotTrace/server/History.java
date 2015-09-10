package jetbrains.buildServer.dotTrace.server;

import java.util.List;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.jetbrains.annotations.NotNull;

public interface History {
  @NotNull
  Iterable<HistoryElement> getElements(@NotNull final List<SFinishedBuild> builds);
}
