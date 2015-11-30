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
  private static final BigDecimal MEASURED_TOTAL_TIME = new BigDecimal(12);
  private static final BigDecimal MEASURED_OWN_TIME = new BigDecimal(34);
  private static final ThresholdValue TOTAL_TIME_THRESHOLD = new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(10));
  private static final ThresholdValue OWN_TIME_THRESHOLD = new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(20));
  private static final BigDecimal PREV_TOTAL_TIME = new BigDecimal(1);
  private static final BigDecimal PREV_OWN_TIME = new BigDecimal(2);
  private static final String TOTAL_TIME_THRESHOLD_STR = "L10";
  private static final String OWN_TIME_THRESHOLD_STR = "F20";
  private static final String MEASURED_TOTAL_TIME_STR = "12";
  private static final String MEASURED_OWN_TIME_STR = "34";

  private Mockery myCtx;
  private BigDecimalParser myBigDecimalParser;
  private BeanFactory myBeanFactory;
  private ValueAggregatorFactory myValueAggregatorFactory;
  private ValueAggregator myValueAggregatorFirst;
  private ValueAggregator myValueAggregatorLast;
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

    myHistoryElement1 = myCtx.mock(HistoryElement.class, "HistoryElement1");
    myHistoryElement2 = myCtx.mock(HistoryElement.class, "HistoryElement2");
  }

  @Test
  public void shouldTryCreateStatistic() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));

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
      will(returnValue(PREV_TOTAL_TIME));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(PREV_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(MEASURED_TOTAL_TIME, MEASURED_OWN_TIME, TOTAL_TIME_THRESHOLD, OWN_TIME_THRESHOLD, PREV_TOTAL_TIME, PREV_OWN_TIME));
  }

  @Test
  public void shouldNotAggregateWhenHistoryReturnsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));

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

      allowing(myValueAggregatorFirst).isCompleted();
      will(returnValue(false));

      oneOf(myHistoryElement2).tryGetValue("totalTimeKey");
      will(returnValue(null));

      oneOf(myHistoryElement2).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(36)));

      oneOf(myValueAggregatorFirst).aggregate(new BigDecimal(36));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(PREV_TOTAL_TIME));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(PREV_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(MEASURED_TOTAL_TIME, MEASURED_OWN_TIME, TOTAL_TIME_THRESHOLD, OWN_TIME_THRESHOLD, PREV_TOTAL_TIME, PREV_OWN_TIME));
  }

  @Test
  public void shouldStopAggregationWhenAllAggregatorAreCompleted() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));

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
      will(returnValue(PREV_TOTAL_TIME));

      oneOf(myValueAggregatorFirst).tryGetAggregatedValue();
      will(returnValue(PREV_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(MEASURED_TOTAL_TIME, MEASURED_OWN_TIME, TOTAL_TIME_THRESHOLD, OWN_TIME_THRESHOLD, PREV_TOTAL_TIME, PREV_OWN_TIME));
  }

  @Test
  public void shouldTryCreateStatisticWhenNoHistory() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.FIRST);
      will(returnValue(myValueAggregatorFirst));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));

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
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Collections.<HistoryElement>emptyList());

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(MEASURED_TOTAL_TIME, MEASURED_OWN_TIME, TOTAL_TIME_THRESHOLD, OWN_TIME_THRESHOLD, null, null));
  }

  @Test
  public void shouldNotCreateStatisticWhenOwnTimeThresholdIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, "", MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(
      myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldNotCreateStatisticWhenTotalTimeThresholdIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", "", OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldNotCreateStatisticWhenMeasuredOwnTimeIsNull() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal("");
      will(returnValue(null));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, MEASURED_TOTAL_TIME_STR, ""), Arrays.asList(myHistoryElement1, myHistoryElement2));

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

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, OWN_TIME_THRESHOLD_STR, "", MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(null);
  }

  @Test
  public void shouldUseAbsoluteValueAsExpectedWhenAbsoluteThreshold() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBeanFactory).getBean(ValueAggregatorFactory.class);
      will(returnValue(myValueAggregatorFactory));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.LAST);
      will(returnValue(myValueAggregatorLast));

      oneOf(myValueAggregatorFactory).tryCreate(ThresholdValueType.ABSOLUTE);
      will(returnValue(null));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_TOTAL_TIME_STR);
      will(returnValue(MEASURED_TOTAL_TIME));

      oneOf(myBigDecimalParser).tryParseBigDecimal(MEASURED_OWN_TIME_STR);
      will(returnValue(MEASURED_OWN_TIME));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("totalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("ownTimeKey"));

      oneOf(myHistoryElement1).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(10)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(10));

      oneOf(myHistoryElement1).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(32)));

      allowing(myValueAggregatorLast).isCompleted();
      will(returnValue(false));

      oneOf(myHistoryElement2).tryGetValue("totalTimeKey");
      will(returnValue(new BigDecimal(14)));

      oneOf(myValueAggregatorLast).aggregate(new BigDecimal(14));

      oneOf(myHistoryElement2).tryGetValue("ownTimeKey");
      will(returnValue(new BigDecimal(36)));

      oneOf(myValueAggregatorLast).tryGetAggregatedValue();
      will(returnValue(PREV_TOTAL_TIME));
    }});

    // When
    final StatisticProvider instance = createInstance();
    final Statistic statistic = instance.tryCreateStatistic(new StatisticMessage("method1", TOTAL_TIME_THRESHOLD_STR, "99", MEASURED_TOTAL_TIME_STR, MEASURED_OWN_TIME_STR), Arrays.asList(myHistoryElement1, myHistoryElement2));

    // Then
    myCtx.assertIsSatisfied();
    then(statistic).isEqualTo(new Statistic(MEASURED_TOTAL_TIME, MEASURED_OWN_TIME, TOTAL_TIME_THRESHOLD, new ThresholdValue(ThresholdValueType.ABSOLUTE, new BigDecimal(99)), PREV_TOTAL_TIME, new BigDecimal(99)));
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
