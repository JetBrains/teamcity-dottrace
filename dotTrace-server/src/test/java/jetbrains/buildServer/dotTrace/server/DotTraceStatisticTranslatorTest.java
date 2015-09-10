package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.messages.serviceMessages.Message;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage;
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTranslator;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.ServerExtensionHolder;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class DotTraceStatisticTranslatorTest {
  private Mockery myCtx;
  private ServerExtensionHolder myServerExtensionHolder;
  private BuildDataStorage myBuildDataStorage;
  private MetricComparer myMetricComparer;
  private SRunningBuild myRunningBuild;
  private BuildMessage1 buildMessage1;
  private SBuildType myBuildType;
  private SFinishedBuild myBuild1;
  private SFinishedBuild myBuild2;
  private StatisticKeyFactory myStatisticKeyFactory;
  private StatisticProvider myStatisticProvider;
  private History myHistory;
  private HistoryElement myHistoryElement1;
  private HistoryElement myHistoryElement2;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    myServerExtensionHolder = myCtx.mock(ServerExtensionHolder.class);
    myBuildDataStorage = myCtx.mock(BuildDataStorage.class);
    myMetricComparer = myCtx.mock(MetricComparer.class);
    myStatisticKeyFactory = myCtx.mock(StatisticKeyFactory.class);
    myStatisticProvider = myCtx.mock(StatisticProvider.class);
    myHistory = myCtx.mock(History.class);
    myRunningBuild = myCtx.mock(SRunningBuild.class);
    myBuildType = myCtx.mock(SBuildType.class);

    buildMessage1 = new BuildMessage1("sourceId", "typeId", Status.NORMAL, new Date(1234567), "value", Arrays.asList("a", "b"));
    myBuild1 = myCtx.mock(SFinishedBuild.class, "Build1");
    myBuild2 = myCtx.mock(SFinishedBuild.class, "Build2");

    myHistoryElement1 = myCtx.mock(HistoryElement.class, "HistoryElement1");
    myHistoryElement2 = myCtx.mock(HistoryElement.class, "HistoryElement2");
  }

  @Test
  public void shouldTranslate() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");
    final Statistic statistic = new Statistic(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4), new BigDecimal(5), new BigDecimal(6));

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(statistic));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(5), new BigDecimal(1), new BigDecimal(3));
      will(returnValue(false));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(6), new BigDecimal(2), new BigDecimal(4));
      will(returnValue(false));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("TotalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("OwnTimeKey"));

      oneOf(myRunningBuild).getBuildId();
      will(returnValue(33L));

      oneOf(myBuildDataStorage).publishValue("TotalTimeKey", 33L, new BigDecimal(1));
      oneOf(myBuildDataStorage).publishValue("OwnTimeKey", 33L, new BigDecimal(2));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(3);
    then(messages.get(0)).isEqualTo(buildMessage1);

    final BuildMessage1 message1 = messages.get(1);
    then(message1.getSourceId()).isEqualTo(buildMessage1.getSourceId());
    then(message1.getTypeId()).isEqualTo(buildMessage1.getTypeId());
    then(message1.getStatus()).isEqualTo(Status.FAILURE);
    then(message1.getTimestamp()).isEqualTo(buildMessage1.getTimestamp());
    then(message1.getValue()).isNotNull();
    then(message1.getValue()).isInstanceOf(String.class);

    final BuildMessage1 message2 = messages.get(2);
    then(message2.getSourceId()).isEqualTo(buildMessage1.getSourceId());
    then(message2.getTypeId()).isEqualTo(buildMessage1.getTypeId());
    then(message2.getStatus()).isEqualTo(Status.FAILURE);
    then(message2.getTimestamp()).isEqualTo(buildMessage1.getTimestamp());
    then(message2.getValue()).isNotNull();
    then(message2.getValue()).isInstanceOf(String.class);
  }

  @Test
  public void shouldTranslateWhenPrevValuesAreNull() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");
    final Statistic statistic = new Statistic(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4), null, null);

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(statistic));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(null, new BigDecimal(1), new BigDecimal(3));
      will(returnValue(false));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(null, new BigDecimal(2), new BigDecimal(4));
      will(returnValue(false));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("TotalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("OwnTimeKey"));

      oneOf(myRunningBuild).getBuildId();
      will(returnValue(33L));

      oneOf(myBuildDataStorage).publishValue("TotalTimeKey", 33L, new BigDecimal(1));
      oneOf(myBuildDataStorage).publishValue("OwnTimeKey", 33L, new BigDecimal(2));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(3);
    then(messages.get(0)).isEqualTo(buildMessage1);

    final BuildMessage1 message1 = messages.get(1);
    then(message1.getSourceId()).isEqualTo(buildMessage1.getSourceId());
    then(message1.getTypeId()).isEqualTo(buildMessage1.getTypeId());
    then(message1.getStatus()).isEqualTo(Status.FAILURE);
    then(message1.getTimestamp()).isEqualTo(buildMessage1.getTimestamp());
    then(message1.getValue()).isNotNull();
    then(message1.getValue()).isInstanceOf(String.class);

    final BuildMessage1 message2 = messages.get(2);
    then(message2.getSourceId()).isEqualTo(buildMessage1.getSourceId());
    then(message2.getTypeId()).isEqualTo(buildMessage1.getTypeId());
    then(message2.getStatus()).isEqualTo(Status.FAILURE);
    then(message2.getTimestamp()).isEqualTo(buildMessage1.getTimestamp());
    then(message2.getValue()).isNotNull();
    then(message2.getValue()).isInstanceOf(String.class);
  }

  @Test
  public void shouldNotGenerateMessageWhenTotalTimeWithinThresholds() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");
    final Statistic statistic = new Statistic(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4), new BigDecimal(5), new BigDecimal(6));

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(statistic));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(5), new BigDecimal(1), new BigDecimal(3));
      will(returnValue(false));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(6), new BigDecimal(2), new BigDecimal(4));
      will(returnValue(true));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("TotalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("OwnTimeKey"));

      oneOf(myRunningBuild).getBuildId();
      will(returnValue(33L));

      oneOf(myBuildDataStorage).publishValue("TotalTimeKey", 33L, new BigDecimal(1));
      oneOf(myBuildDataStorage).publishValue("OwnTimeKey", 33L, new BigDecimal(2));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(2);
  }

  @Test
  public void shouldNotGenerateMessageWhenOwnTimeWithinThresholds() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");
    final Statistic statistic = new Statistic(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4), new BigDecimal(5), new BigDecimal(6));

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(statistic));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(5), new BigDecimal(1), new BigDecimal(3));
      will(returnValue(true));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(6), new BigDecimal(2), new BigDecimal(4));
      will(returnValue(false));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("TotalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("OwnTimeKey"));

      oneOf(myRunningBuild).getBuildId();
      will(returnValue(33L));

      oneOf(myBuildDataStorage).publishValue("TotalTimeKey", 33L, new BigDecimal(1));
      oneOf(myBuildDataStorage).publishValue("OwnTimeKey", 33L, new BigDecimal(2));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(2);
  }

  @Test
  public void shouldNotGenerateMessageWhenValuesWithinThresholds() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");
    final Statistic statistic = new Statistic(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4), new BigDecimal(5), new BigDecimal(6));

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(statistic));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(5), new BigDecimal(1), new BigDecimal(3));
      will(returnValue(true));

      oneOf(myMetricComparer).isMeasuredValueWithinThresholds(new BigDecimal(6), new BigDecimal(2), new BigDecimal(4));
      will(returnValue(true));

      oneOf(myStatisticKeyFactory).createTotalTimeKey("method1");
      will(returnValue("TotalTimeKey"));

      oneOf(myStatisticKeyFactory).createOwnTimeKey("method1");
      will(returnValue("OwnTimeKey"));

      oneOf(myRunningBuild).getBuildId();
      will(returnValue(33L));

      oneOf(myBuildDataStorage).publishValue("TotalTimeKey", 33L, new BigDecimal(1));
      oneOf(myBuildDataStorage).publishValue("OwnTimeKey", 33L, new BigDecimal(2));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(1);
  }

  @Test
  public void shouldNotGenerateMessageAndShouldNotPublishValuesWhenStatistic() {
    // Given
    final List<SFinishedBuild> builds = Arrays.asList(myBuild1, myBuild2);
    final List<HistoryElement> historyElements = Arrays.asList(myHistoryElement1, myHistoryElement2);
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(myBuildType));

      oneOf(myBuildType).getHistory();
      will(returnValue(builds));

      oneOf(myHistory).getElements(builds);
      will(returnValue(historyElements));

      oneOf(myStatisticProvider).tryCreateStatistic(statisticMessage, historyElements);
      will(returnValue(null));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(1);
  }

  @Test
  public void shouldNotGenerateMessageAndShouldNotPublishValuesWhenBuildTypeIsNull() {
    // Given
    final StatisticMessage statisticMessage = new StatisticMessage("method1", "L10", "F20", "12", "34");

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()),
                                                       with(any(ServiceMessageTranslator.class)));

      oneOf(myRunningBuild).getBuildType();
      will(returnValue(null));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, statisticMessage);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(1);
  }

  @Test
  public void shouldNotGenerateMessageAndShouldNotPublishValuesWhenIsNotStatisticMessage() {
    // Given
    final ServiceMessage message = new MyMessage();

    myCtx.checking(new Expectations() {{
      oneOf(myServerExtensionHolder).registerExtension(with(ServiceMessageTranslator.class), with(DotTraceStatisticTranslator.class.getName()), with(any(ServiceMessageTranslator.class)));
    }});

    // When
    final ServiceMessageTranslator instance = createInstance();
    final List<BuildMessage1> messages = instance.translate(myRunningBuild, buildMessage1, message);

    // Then
    myCtx.assertIsSatisfied();
    then(messages.size()).isEqualTo(1);
  }

  @NotNull
  private ServiceMessageTranslator createInstance()
  {
    return new DotTraceStatisticTranslator(
      myServerExtensionHolder,
      myBuildDataStorage,
      myMetricComparer,
      myStatisticKeyFactory,
      myStatisticProvider,
      myHistory);
  }

  private class MyMessage extends Message{
    public MyMessage() {
      super("abc", "NORMAL", null);
    }
  }
}
