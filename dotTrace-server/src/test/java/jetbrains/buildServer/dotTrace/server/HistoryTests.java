package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jetbrains.buildServer.dotTrace.StatisticMessage;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.statistics.build.BuildDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.beans.factory.BeanFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class HistoryTests {
  private Mockery myCtx;
  private BuildDataStorage myBuildDataStorage;
  private SFinishedBuild myBuild1;
  private SFinishedBuild myBuild2;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    myBuildDataStorage = myCtx.mock(BuildDataStorage.class);
    myBuild1 = myCtx.mock(SFinishedBuild.class, "Build1");
    myBuild2 = myCtx.mock(SFinishedBuild.class, "Build2");
  }

  @Test
  public void shouldGetElements() {
    // Given
    final History instance = createInstance();
    final List<HistoryElement> elements = new ArrayList<HistoryElement>();

    // When
    for(HistoryElement element: instance.getElements(Arrays.asList(myBuild1, myBuild2))) {
      elements.add(element);
    }

    // Then
    myCtx.assertIsSatisfied();
    then(elements.size()).isEqualTo(2);
  }

  @Test
  public void shouldGetElementsWhichImplementHistoryElement() {
    // Given
    myCtx.checking(new Expectations() {{
      oneOf(myBuildDataStorage).getValues(myBuild1);
      will(returnValue(Collections.singletonMap("abc", new BigDecimal(9))));
    }});

    // When
    final History instance = createInstance();
    final List<HistoryElement> elements = new ArrayList<HistoryElement>();
    for(HistoryElement element: instance.getElements(Arrays.asList(myBuild1))) {
      elements.add(element);
    }

    final HistoryElement element = elements.get(0);
    final BigDecimal actualValue = element.tryGetValue("abc");

    // Then
    myCtx.assertIsSatisfied();
    then(elements.size()).isEqualTo(1);
    then(actualValue).isEqualTo(new BigDecimal(9));
  }

  @NotNull
  private History createInstance() {
    return new HistoryImpl(myBuildDataStorage);
  }
}
