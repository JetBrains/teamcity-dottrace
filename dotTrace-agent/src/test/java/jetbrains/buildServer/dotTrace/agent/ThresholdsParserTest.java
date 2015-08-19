package jetbrains.buildServer.dotTrace.agent;

import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.BuildException;
import jetbrains.buildServer.dotNet.buildRunner.agent.TextParser;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ThresholdsParserTest {
  private static final String ourlineSeparator = System.getProperty("line.separator");

  @DataProvider(name = "parseThresholdsFromStringCases")
  public Object[][] getParseThresholdsFromStringCases() {
    return new Object[][] {
      { "IntegrationTests.MainTests.Test1 100 F15", new Thresholds(Arrays.asList(new Threshold("IntegrationTests.MainTests.Test1", "100", "F15"))), false },
      { "IntegrationTests.MainTests.Test1 100 F15" + ourlineSeparator + "IntegrationTests.MainTests.Test2 200 A15", new Thresholds(Arrays.asList(new Threshold("IntegrationTests.MainTests.Test1", "100", "F15"), new Threshold("IntegrationTests.MainTests.Test2", "200", "A15"))), false },
      { "", new Thresholds(Collections.<Threshold>emptyList()), false },
      { "IntegrationTests.MainTests.Test1 100 F15 300", new Thresholds(Collections.<Threshold>emptyList()), true },
      { "IntegrationTests.MainTests.Test1 100", new Thresholds(Collections.<Threshold>emptyList()), true },
      { "IntegrationTests.MainTests.Test1", new Thresholds(Collections.<Threshold>emptyList()), true },
      { "IntegrationTests.MainTests.Test1" + ourlineSeparator + "IntegrationTests.MainTests.Test2 200 A15", new Thresholds(Collections.<Threshold>emptyList()), true },
    };
  }

  @Test(dataProvider = "parseThresholdsFromStringCases")
  public void shouldParseThresholdsFromString(@NotNull final String text, @NotNull final Thresholds expectedThresholds, boolean expectedExceptionThrown)
  {
    // Given
    final TextParser<Thresholds> instance = createInstance();

    // When
    Thresholds actualThresholds = null;
    boolean actualExceptionThrown = false;

    try {
      actualThresholds = instance.parse(text);
    }
    catch (BuildException ex) {
      actualExceptionThrown = true;
    }

    // Then
    if(!expectedExceptionThrown) {
      then(actualThresholds.getThresholds()).containsExactlyElementsOf(expectedThresholds.getThresholds());
    }

    then(actualExceptionThrown).isEqualTo(expectedExceptionThrown);
  }

  @NotNull
  private TextParser<Thresholds> createInstance()
  {
    return new ThresholdsParser();
  }
}