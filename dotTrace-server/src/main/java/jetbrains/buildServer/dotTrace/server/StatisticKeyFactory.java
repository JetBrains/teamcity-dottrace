

package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;

public interface StatisticKeyFactory {
  @NotNull
  String createTotalTimeKey(@NotNull final String methodName);

  @NotNull
  String createOwnTimeKey(@NotNull final String methodName);
}