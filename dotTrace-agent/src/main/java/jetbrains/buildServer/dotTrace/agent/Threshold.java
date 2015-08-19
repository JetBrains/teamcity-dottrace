package jetbrains.buildServer.dotTrace.agent;

import org.jetbrains.annotations.NotNull;

public class Threshold {
  private final String myMethodName;
  private final String myTotalTime;
  private final String myOwnTime;

  public Threshold(
    @NotNull final String methodName,
    @NotNull final String totalTime,
    @NotNull final String ownTime) {
    myMethodName = methodName;
    myTotalTime = totalTime;
    myOwnTime = ownTime;
  }

  @NotNull
  public String getMethodName() {
    return myMethodName;
  }

  @NotNull
  public String getTotalTime() {
    return myTotalTime;
  }

  @NotNull
  public String getOwnTime() {
    return myOwnTime;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Threshold threshold = (Threshold)o;

    if (!getMethodName().equals(threshold.getMethodName())) return false;
    if (!getTotalTime().equals(threshold.getTotalTime())) return false;
    return getOwnTime().equals(threshold.getOwnTime());

  }

  @Override
  public int hashCode() {
    int result = getMethodName().hashCode();
    result = 31 * result + getTotalTime().hashCode();
    result = 31 * result + getOwnTime().hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Threshold{" +
           "MethodName='" + myMethodName + '\'' +
           ", TotalTime='" + myTotalTime + '\'' +
           ", OwnTime='" + myOwnTime + '\'' +
           '}';
  }
}
