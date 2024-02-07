

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