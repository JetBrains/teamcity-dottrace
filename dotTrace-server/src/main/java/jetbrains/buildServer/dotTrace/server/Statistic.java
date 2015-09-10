package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class Statistic {
  private final BigDecimal myMeasuredTotalTime;
  private final BigDecimal myMeasuredOwnTime;
  private final BigDecimal myTotalTimeThreshold;
  private final BigDecimal myOwnTimeThreshold;
  private final BigDecimal myPrevTotalTime;
  private final BigDecimal myPrevOwnTime;

  public Statistic(@NotNull final BigDecimal measuredTotalTime,
                   @NotNull final BigDecimal measuredOwnTime,
                   @NotNull final BigDecimal totalTimeThreshold,
                   @NotNull final BigDecimal ownTimeThreshold,
                   @Nullable final BigDecimal prevTotalTime,
                   @Nullable final BigDecimal prevOwnTime) {
    myMeasuredTotalTime = measuredTotalTime;
    myMeasuredOwnTime = measuredOwnTime;
    myTotalTimeThreshold = totalTimeThreshold;
    myOwnTimeThreshold = ownTimeThreshold;
    myPrevTotalTime = prevTotalTime;
    myPrevOwnTime = prevOwnTime;
  }

  @NotNull
  public BigDecimal getMeasuredTotalTime() {
    return myMeasuredTotalTime;
  }

  @NotNull
  public BigDecimal getMeasuredOwnTime() {
    return myMeasuredOwnTime;
  }

  @NotNull
  public BigDecimal getTotalTimeThreshold() {
    return myTotalTimeThreshold;
  }

  @NotNull
  public BigDecimal getOwnTimeThreshold() {
    return myOwnTimeThreshold;
  }

  @Nullable
  public BigDecimal getPrevTotalTime() {
    return myPrevTotalTime;
  }

  @Nullable
  public BigDecimal getPrevOwnTime() {
    return myPrevOwnTime;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Statistic statistic = (Statistic)o;

    if (!getMeasuredTotalTime().equals(statistic.getMeasuredTotalTime())) return false;
    if (!getMeasuredOwnTime().equals(statistic.getMeasuredOwnTime())) return false;
    if (!getTotalTimeThreshold().equals(statistic.getTotalTimeThreshold())) return false;
    if (!getOwnTimeThreshold().equals(statistic.getOwnTimeThreshold())) return false;
    if (getPrevTotalTime() != null ? !getPrevTotalTime().equals(statistic.getPrevTotalTime()) : statistic.getPrevTotalTime() != null) return false;
    return !(getPrevOwnTime() != null ? !getPrevOwnTime().equals(statistic.getPrevOwnTime()) : statistic.getPrevOwnTime() != null);

  }

  @Override
  public int hashCode() {
    int result = getMeasuredTotalTime().hashCode();
    result = 31 * result + getMeasuredOwnTime().hashCode();
    result = 31 * result + getTotalTimeThreshold().hashCode();
    result = 31 * result + getOwnTimeThreshold().hashCode();
    result = 31 * result + (getPrevTotalTime() != null ? getPrevTotalTime().hashCode() : 0);
    result = 31 * result + (getPrevOwnTime() != null ? getPrevOwnTime().hashCode() : 0);
    return result;
  }
}
