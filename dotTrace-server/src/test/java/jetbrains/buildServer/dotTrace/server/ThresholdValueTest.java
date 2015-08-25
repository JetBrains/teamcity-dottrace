package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ThresholdValueTest {
  @DataProvider(name = "parseValueFromTextCases")
  public Object[][] getParseValueFromTextCases() {
    return new Object[][] {
      { "L50", new ThresholdValue(ThresholdValueType.LAST, new BigDecimal(50))},
      { "F50", new ThresholdValue(ThresholdValueType.FIRST, new BigDecimal(50))},
      { "A50", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(50))},
      { "A100", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(100))},
      { "A0", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(0))},
      { "A200", new ThresholdValue(ThresholdValueType.AVERAGE, new BigDecimal(200))},
      { "A", null},
      { "F", null},
      { "L", null},
      { "50", null},
      { "A-50", null},
      { "-50", null},
    };
  }

  @Test(dataProvider = "parseValueFromTextCases")
  public void shouldParseValueFromText(@Nullable final String valueStr, @Nullable ThresholdValue expectedValue) {
    // Given

    // When
    final ThresholdValue actualValue = ThresholdValue.tryParse(valueStr);

    // Then
    then(actualValue).isEqualTo(expectedValue);
  }
}
