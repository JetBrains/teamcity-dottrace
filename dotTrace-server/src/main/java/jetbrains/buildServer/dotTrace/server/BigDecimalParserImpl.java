/*
 * Copyright 2000-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.dotTrace.server;

import com.intellij.openapi.util.text.StringUtil;
import java.math.BigDecimal;
import org.jetbrains.annotations.Nullable;

public class BigDecimalParserImpl implements BigDecimalParser {
  @Nullable
  @Override
  public BigDecimal tryParseBigDecimal(@Nullable final String valueStr) {
    if(StringUtil.isEmptyOrSpaces(valueStr)) {
      return null;
    }

    try {
      return new BigDecimal(valueStr);
    }
    catch (NumberFormatException ignored) {
      return null;
    }
  }
}
