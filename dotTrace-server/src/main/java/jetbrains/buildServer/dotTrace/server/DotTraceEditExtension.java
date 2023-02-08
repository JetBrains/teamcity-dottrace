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

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunTypeExtension;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.util.positioning.PositionAware;
import jetbrains.buildServer.util.positioning.PositionConstraint;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

public class DotTraceEditExtension extends RunTypeExtension implements PositionAware {
  private static final String PATH_NOT_SPECIFIED_ERROR_MESSAGE = "The path to dotTrace must be specified.";
  private static final String THRESHOLDS_NOT_SPECIFIED_ERROR_MESSAGE = "The threshold values must be specified.";
  private static final String PROFILE_CHILD_PROCESSES_DEFAULT_VAL = "true";
  private static final List<String> ourRunTypes = Arrays.asList("MSBuild", "NAnt", "NUnit", "jetbrains.mspec", "jetbrains.dotNetGenericRunner", "jetbrains.xunit", "VisualStudioTest", "MSTest", "VSTest", "dotnet.cli", "dotnet");
  private final String myViewUrl;
  private final String myEditUrl;

  public DotTraceEditExtension(
          @NotNull final PluginDescriptor descriptor,
          @NotNull final WebControllerManager wcm) {
    myViewUrl = registerView(descriptor, wcm, "dotTraceView.html", "viewDotTrace.jsp");
    myEditUrl = registerView(descriptor, wcm, "dotTraceEdit.html", "editDotTrace.jsp");
  }

  @Override
  @NotNull
  public String getOrderId() {
    return "dotTrace";
  }

  @Override
  @NotNull
  public PositionConstraint getConstraint() {
    return PositionConstraint.last();
  }

  @Override
  public Collection<String> getRunTypes() {
    return Collections.unmodifiableCollection(ourRunTypes);
  }

  @Nullable
  @Override
  public PropertiesProcessor getRunnerPropertiesProcessor() {
    return new PropertiesProcessor() {
      @Override
      public Collection<InvalidProperty> process(final Map<String, String> properties) {
        final ArrayList<InvalidProperty> result = new ArrayList<InvalidProperty>();

        final boolean useDotTrace = StringUtil.isTrue(properties.get(DotTraceBean.Shared.getUseDotTraceKey()));
        if(useDotTrace && StringUtil.isEmptyOrSpaces(properties.get(DotTraceBean.Shared.getPathKey()))) {
          result.add(new InvalidProperty(DotTraceBean.Shared.getPathKey(), PATH_NOT_SPECIFIED_ERROR_MESSAGE));
        }

        if(useDotTrace && StringUtil.isEmptyOrSpaces(properties.get(DotTraceBean.Shared.getThresholdsKey()))) {
          result.add(new InvalidProperty(DotTraceBean.Shared.getThresholdsKey(), THRESHOLDS_NOT_SPECIFIED_ERROR_MESSAGE));
        }

        return result;
      }
    };
  }

  @Override
  public String getEditRunnerParamsJspFilePath() {
    return myEditUrl;
  }

  @Override
  public String getViewRunnerParamsJspFilePath() {
    return myViewUrl;
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultRunnerProperties() {
    return CollectionsUtil.asMap(
      DotTraceBean.Shared.getMeasureTypeKey(), DotTraceBean.Shared.getMeasureTypes()[0].getValue(),
      DotTraceBean.Shared.getProfileChildProcessesKey(), PROFILE_CHILD_PROCESSES_DEFAULT_VAL);
  }

  private String registerView(@NotNull final PluginDescriptor description,
                              @NotNull final WebControllerManager wcm,
                              @NotNull final String url,
                              @NotNull final String jsp) {
    final String actualUrl = description.getPluginResourcesPath(url);
    final String actualJsp = description.getPluginResourcesPath(jsp);

    wcm.registerController(actualUrl, new BaseController() {
      @Override
      protected ModelAndView doHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {
        return new ModelAndView(actualJsp);
      }
    });
    return actualUrl;
  }
}
