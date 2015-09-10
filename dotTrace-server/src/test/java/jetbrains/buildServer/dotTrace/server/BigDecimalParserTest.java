package jetbrains.buildServer.dotTrace.server;

import java.math.BigDecimal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class BigDecimalParserTest {
  @DataProvider(name = "parseValueFromTextCases")
  public Object[][] getParseValueFromTextCases() {
    return new Object[][] {
      { "50", new BigDecimal(50)},
      { "50.1", new BigDecimal("50.1")},
      { null, null},
      { "", null},
      { " ", null},
      { "aa", null},
      { "aa50", null},
    };
  }

  @Test(dataProvider = "parseValueFromTextCases")
  public void shouldParseValueFromText(@Nullable final String valueStr, @Nullable BigDecimal expectedValue) {
    // Given

    // When
    BigDecimalParser instance = createInstance();
    final BigDecimal actualValue = instance.tryParseBigDecimal(valueStr);

    // Then
    then(actualValue).isEqualTo(expectedValue);
  }


  @NotNull
  private BigDecimalParser createInstance()
  {
    return new BigDecimalParserImpl();
  }
}
