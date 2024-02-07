

package jetbrains.buildServer.dotTrace.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class StatisticKeyFactoryTest {
  @DataProvider(name = "createOwnTimeKeyCases")
  public Object[][] getCreateOwnTimeKeyCases() {
    return new Object[][] {
      { "method1", "method1:" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { "method1:aaa", "method1:aaa:" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { "", ":" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY},
      { " ", " :" + StatisticKeyFactoryImpl.DOT_TRACE_OWN_TIME_STATISTIC_KEY}
    };
  }

  @Test(dataProvider = "createOwnTimeKeyCases")
  public void shouldCreateOwnTimeKey(@NotNull final String methodName, @Nullable String expectedKey) {
    // Given

    // When
    StatisticKeyFactory instance = createInstance();
    final String actualKey = instance.createOwnTimeKey(methodName);

    // Then
    then(actualKey).isEqualTo(expectedKey);
  }

  @DataProvider(name = "createTotalTimeKeyCases")
  public Object[][] getCreateTotalTimeKeyCases() {
    return new Object[][] {
      { "method1", "method1:" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { "method1:aaa", "method1:aaa:" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { "", ":" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY},
      { " ", " :" + StatisticKeyFactoryImpl.DOT_TRACE_TOTAL_TIME_STATISTIC_KEY}
    };
  }

  @Test(dataProvider = "createTotalTimeKeyCases")
  public void shouldCreateTotalTimeKey(@NotNull final String methodName, @Nullable String expectedKey) {
    // Given

    // When
    StatisticKeyFactory instance = createInstance();
    final String actualKey = instance.createTotalTimeKey(methodName);

    // Then
    then(actualKey).isEqualTo(expectedKey);
  }

  @NotNull
  private StatisticKeyFactory createInstance()
  {
    return new StatisticKeyFactoryImpl();
  }
}