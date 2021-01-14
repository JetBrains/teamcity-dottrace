/*
 * Copyright 2000-2021 JetBrains s.r.o.
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

package jetbrains.buildServer.dotTrace;

import org.jetbrains.annotations.NotNull;

public enum MeasureType {
  SAMPLING("sampling", "Sampling", "Sampling"),
  TRACING("tracing", "Tracing", ""),
  LINE_BY_LINE("line-by-line", "Line-by-line", "TracingInject");

  private final String myValue;
  private final String myDescription;
  private final String myId;

  MeasureType(@NotNull final String value, @NotNull final String description, @NotNull final String id) {
    myValue = value;
    myDescription = description;
    myId = id;
  }

  @NotNull
  public String getValue() {
    return myValue;
  }

  @NotNull
  public String getDescription() {
    return myDescription;
  }

  @NotNull
  @Override
  public String toString() {
    return myDescription;
  }

  @NotNull
  public String getId() {
    return myId;
  }
}