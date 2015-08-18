package jetbrains.buildServer.dotTrace.agent;

import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineSetup;
import org.jetbrains.annotations.NotNull;

public class DotTraceContext {
  private final CommandLineSetup myBaseSetup;

  public DotTraceContext(
    @NotNull final CommandLineSetup baseSetup) {
    myBaseSetup = baseSetup;
  }

  @NotNull
  public CommandLineSetup getBaseSetup() {
    return myBaseSetup;
  }

  @Override
  public String toString() {
    return "DotTraceContext{" +
           "myBaseSetup=" + myBaseSetup +
           '}';
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final DotTraceContext that = (DotTraceContext)o;

    return getBaseSetup().equals(that.getBaseSetup());

  }

  @Override
  public int hashCode() {
    return getBaseSetup().hashCode();
  }
}
