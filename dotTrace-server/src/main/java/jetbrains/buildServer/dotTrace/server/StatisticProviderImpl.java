package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import javax.annotation.Nullable;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;

public class StatisticProviderImpl implements StatisticProvider {
  private final BigDecimalParser myBigDecimalParser;
  private final StatisticKeyFactory myStatisticKeyFactory;
  private final BeanFactory myBeanFactory;

  public StatisticProviderImpl(
    @NotNull final BigDecimalParser bigDecimalParser,
    @NotNull final StatisticKeyFactory statisticKeyFactory,
    @NotNull final BeanFactory beanFactory) {
    myBigDecimalParser = bigDecimalParser;
    myStatisticKeyFactory = statisticKeyFactory;
    myBeanFactory = beanFactory;
  }

  @Nullable
  @Override
  public Statistic tryCreateStatistic(@NotNull final StatisticMessage statisticMessage, @NotNull final Iterable<HistoryElement> historyProviders) {
    final String methodName = statisticMessage.getMethodName();
    @Nullable final BigDecimal measuredTotalTime = myBigDecimalParser.tryParseBigDecimal(statisticMessage.getMeasuredTotalTime());
    @Nullable final BigDecimal measuredOwnTime = myBigDecimalParser.tryParseBigDecimal(statisticMessage.getMeasuredOwnTime());
    @Nullable final ThresholdValue totalTimeThreshold = ThresholdValue.tryParse(statisticMessage.getTotalTimeThreshold());
    @Nullable final ThresholdValue ownTimeThreshold = ThresholdValue.tryParse(statisticMessage.getOwnTimeThreshold());
    if(measuredTotalTime == null || measuredOwnTime == null || totalTimeThreshold == null || ownTimeThreshold == null) {
      return null;
    }

    final String totalTimeKey = myStatisticKeyFactory.createTotalTimeKey(methodName);
    final String ownTimeKey = myStatisticKeyFactory.createOwnTimeKey(methodName);

    final ValueAggregatorFactory valueAggregatorFactory = myBeanFactory.getBean(ValueAggregatorFactory.class);
    final ValueAggregator totalTimeAgg = valueAggregatorFactory.create(totalTimeThreshold.getType());
    final ValueAggregator ownTimeAgg = valueAggregatorFactory.create(ownTimeThreshold.getType());

    for(HistoryElement historyElement : historyProviders) {
      @Nullable final BigDecimal totalTimeVal = historyElement.tryGetValue(totalTimeKey);
      if(totalTimeVal != null) {
        totalTimeAgg.aggregate(totalTimeVal);
      }

      @Nullable final BigDecimal ownTimeVal = historyElement.tryGetValue(ownTimeKey);
      if(ownTimeVal != null) {
        ownTimeAgg.aggregate(ownTimeVal);
      }

      if(totalTimeAgg.isCompleted() && ownTimeAgg.isCompleted()) {
        break;
      }
    }

    @Nullable final BigDecimal prevTotalTime = totalTimeAgg.tryGetAggregatedValue();
    @Nullable final BigDecimal prevOwnTime = ownTimeAgg.tryGetAggregatedValue();

    return new Statistic(measuredTotalTime, measuredOwnTime, totalTimeThreshold.getValue(), ownTimeThreshold.getValue(), prevTotalTime, prevOwnTime);
  }
}
