package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.beans.factory.BeanFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;


public class StatisticProviderTest {
  private Mockery myCtx;
  private BigDecimalParser myBigDecimalParser;
  private BeanFactory myBeanFactory;
  private ValueAggregatorFactory myValueAggregatorFactory;
  private ValueAggregator myValueAggregatorFirst;
  private ValueAggregator myValueAggregatorLast;
  private ValueAggregator myValueAggregatorAverage;
  private StatisticKeyFactory myStatisticKeyFactory;
  private HistoryElement myHistoryElement1;
  private HistoryElement myHistoryElement2;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    myBigDecimalParser = myCtx.mock(BigDecimalParser.class);
    myStatisticKeyFactory = myCtx.mock(StatisticKeyFactory.class);
    myBeanFactory = myCtx.mock(BeanFactory.class);
    myValueAggregatorFactory = myCtx.mock(ValueAggregatorFactory.class);
    myValueAggregatorFirst = myCtx.mock(ValueAggregator.class, "ValueAggregatorFirst");
    myValueAggregatorLast = myCtx.mock(ValueAggregator.class, "ValueAggregatorLast");
    myValueAggregatorAverage = myCtx.mock(ValueAggregator.class, "ValueAggregatorAverage");

    myHistoryElement1 = myCtx.mock(HistoryElement.class, "HistoryElement1");
    myHistoryElement2 = myCtx.mock(HistoryElement.class, "HistoryElement2");
  }

  @Test
  public void shouldTryCreateStatistic() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("totalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("ownTimeKey"));

      oneOf(myHistoryElement1).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(10)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(10));

      oneOf(myHistoryElement1).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(32)));

      oneOf(myValueAggregatorFirst).aggregate(new BigDecimal(32));

      allowing(myValueAggregatorLast).isCompleted();
      will(returnValue(false));

      allowing(myValueAggregatorFirst).isCompleted();
      will(returnValue(false));

      oneOf(myHistoryElement2).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(14)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(14));

      oneOf(myHistoryElement2).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(36)));

      oneOf(myValueAggregatorFirst).aggregate(new BigDecimal(36));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(1)));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(2)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "F20", "12", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(new BigDecimal(12), new BigDecimal(34), new BigDecimal(10), new BigDecimal(20), new BigDecimal(1), new BigDecimal(2)));
  }

  @Test
  public void shouldNotAggregateWhenHistoryReturnsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.AVERAGE);
      will(returnValue(myValueAggregatorAverage));

      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("totalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("ownTimeKey"));

      oneOf(myHistoryElement1).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(10)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(10));

      oneOf(myHistoryElement1).tryGetValue("ownTimeKey");
      will(returnValue(null));

      allowing(myValueAggregatorLast).isCompleted();
      will(returnValue(false));

      allowing(myValueAggregatorAverage).isCompleted();
      will(returnValue(false));

      oneOf(myHistoryElement2).tryGetValue("totalTimeKey");
      will(returnValue(null));

      oneOf(myHistoryElement2).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(36)));

      oneOf(myValueAggregatorAverage).aggregate(new BigDecimal(36));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(1)));

      oneOf(myValueAggregatorAverage).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(2)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "A20", "12", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(new BigDecimal(12), new BigDecimal(34), new BigDecimal(10), new BigDecimal(20), new BigDecimal(1), new BigDecimal(2)));
  }

  @Test
  public void shouldStopAggregationWhenAllAggregatorAreCompleted() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("totalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("ownTimeKey"));

      oneOf(myHistoryElement1).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(10)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(10));

      oneOf(myHistoryElement1).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(32)));

      oneOf(myValueAggregatorFirst).aggregate(new BigDecimal(32));

      allowing(myValueAggregatorLast).isCompleted();
      will(returnValue(true));

      allowing(myValueAggregatorFirst).isCompleted();
      will(returnValue(true));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(1)));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(new BigDecimal(2)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "F20", "12", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(new BigDecimal(12), new BigDecimal(34), new BigDecimal(10), new BigDecimal(20), new BigDecimal(1), new BigDecimal(2)));
  }

  @Test
  public void shouldTryCreateStatisticWhenNoHistory() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).create(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("totalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("ownTimeKey"));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(null));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(null));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "F20", "12", "34"), Collections.<HistoryElement>emptyList());

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(new BigDecimal(12), new BigDecimal(34), new BigDecimal(10), new BigDecimal(20), null, null));
  }

  @Test
  public void shouldNotCreateStatisticWhenOwnTimeThresholdIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "", "12", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldNotCreateStatisticWhenTotalTimeThresholdIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "", "F20", "12", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldNotCreateStatisticWhenMeasuredOwnTimeIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal("12");
      will(returnValue(new BigDecimal(12)));

      oneOf(myBigDecimalParser).tryParseBigDecimal("");
      will(returnValue(null));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "F20", "12", ""), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldNotCreateStatisticWhenMeasuredTotalTimeIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal("");
      will(returnValue(null));

      oneOf(myBigDecimalParser).tryParseBigDecimal("34");
      will(returnValue(new BigDecimal(34)));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "L10", "F20", "", "34"), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @NotNull
  private StatisticProvider createInstance()
  {
    return new StatisticProviderImpl(
      myBigDecimalParser,
      myStatisticKeyFactory,
      myBeanFactory);
  }
}
