/*
 * Copyright 2000-2020 JetBrains s.r.o.
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

package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.dotTrace.MeasureType;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProjectGenerator implements ResourceGenerator<Context> {
  private static final Map<String, String> outDocumentProperties = new HashMap<String, String>();
  private static final String ROOT_ELEMENT = "root";
  private static final String TYPE_ATTR = "type";
  private static final String TYPE_ELEMENT = "Type";
  private static final String HOST_PARAMETERS_ELEMENT = "HostParameters";
  private static final String TYPE_LOCAL_HOST_PARAMETERS = "LocalHostParameters";
  private static final String ARGUMENT_ELEMENT = "Argument";
  private static final String ARGUMENTS_ELEMENT = "Arguments";
  private static final String FILE_NAME_ELEMENT = "FileName";
  private static final String PROFILE_CHILD_PROCESSES_ELEMENT = "ProfileChildProcesses";
  private static final String WORKING_DIRECTORY_ELEMENT = "WorkingDirectory";
  private static final String INFO_ELEMENT = "Info";
  private static final String MEASURE_TYPE_ELEMENT = "MeasureType";
  private static final String METER_KIND_ELEMENT = "MeterKind";
  private static final String METER_KIND_RDTSC = "Rdtsc";
  private static final String INJECT_INFO_ELEMENT = "InjectInfo";
  private static final String SYMBOL_SEARCH_ELEMENT = "SymbolSearch";
  private static final String SEARCH_PATHS_ELEMENT = "SearchPaths";
  private static final String TYPE_STANDALONE_ARGUMENT = "StandaloneArgument";
  private static final String TYPE_PERFORMANCE_INFO = "PerformanceInfo";
  private static final String SCOPE_ELEMENT = "Scope";
  private static final String PROCESS_FILTERS_ELEMENT = "ProcessFilters";
  private static final String ITEM_ELEMENT = "Item";
  private static final String PATTERN_FILTERS_ELEMENT = "PatternFilters";
  private static final String DENY_ATTRIBUTE_FILTERS_ELEMENT = "DenyAttributeFilters";
  private static final String TRUE_VAL = "True";
  private static final String FALSE_VAL = "False";
  private static final String PROCESS_NAME_FILTER_ELEMENT = "ProcessNameFilter";
  private static final String DENY_PROCESS_FILTER_VAL = "Deny";

  private final TextParser<List<ProcessFilter>> myProcessFiltersParser;
  private final XmlDocumentManager myDocumentManager;
  private final CommandLineArgumentsService myCommandLineArgumentsService;
  private final FileService myFileService;
  private final RunnerParametersService myParametersService;

  public ProjectGenerator(
    @NotNull final TextParser<List<ProcessFilter>> processFiltersParser,
    @NotNull final XmlDocumentManager documentManager,
    @NotNull final CommandLineArgumentsService commandLineArgumentsService,
    @NotNull final FileService fileService,
    @NotNull final RunnerParametersService parametersService) {
    myProcessFiltersParser = processFiltersParser;
    myDocumentManager = documentManager;
    myCommandLineArgumentsService = commandLineArgumentsService;
    myFileService = fileService;
    myParametersService = parametersService;
  }

  @Override
  @NotNull
  public String create(@NotNull final Context ctx) {
    final Document doc = myDocumentManager.createDocument();
    final Element rootElement = doc.createElement(ROOT_ELEMENT);

    final Element hostParametersElement = doc.createElement(HOST_PARAMETERS_ELEMENT);
    hostParametersElement.setAttribute(TYPE_ATTR, TYPE_LOCAL_HOST_PARAMETERS);
    rootElement.appendChild(hostParametersElement);

    final Element argumentElement = doc.createElement(ARGUMENT_ELEMENT);
    argumentElement.setAttribute(TYPE_ATTR, TYPE_STANDALONE_ARGUMENT);
    argumentElement.appendChild(createSimpleElement(doc, ARGUMENTS_ELEMENT, myCommandLineArgumentsService.createCommandLineString(ctx.getBaseSetup().getArgs())));
    argumentElement.appendChild(createSimpleElement(doc, FILE_NAME_ELEMENT, ctx.getBaseSetup().getToolPath()));

    final String profileChildProcessesStr = myParametersService.tryGetRunnerParameter(Constants.PROFILE_CHILD_PROCESSES_VAR);
    final boolean profileChildProcesses = StringUtil.isEmptyOrSpaces(profileChildProcessesStr) || Boolean.parseBoolean(profileChildProcessesStr);

    argumentElement.appendChild(createSimpleElement(doc, PROFILE_CHILD_PROCESSES_ELEMENT, profileChildProcesses ? TRUE_VAL : FALSE_VAL));
    argumentElement.appendChild(createSimpleElement(doc, WORKING_DIRECTORY_ELEMENT, myFileService.getCheckoutDirectory().getPath()));
    rootElement.appendChild(argumentElement);

    final Element argumentScopeElement = doc.createElement(SCOPE_ELEMENT);
    final Element processFiltersElement = doc.createElement(PROCESS_FILTERS_ELEMENT);
    final String processFiltersStr = myParametersService.tryGetRunnerParameter(Constants.PROCESS_FILTERS_VAR);
    if(!StringUtil.isEmptyOrSpaces(processFiltersStr)) {
      final List<ProcessFilter> filters = myProcessFiltersParser.parse(processFiltersStr);
      if(filters.size() > 0) {
        processFiltersElement.appendChild(createSimpleElement(doc, ITEM_ELEMENT, ""));
        for (ProcessFilter filter : filters) {
          final Element itemElement = doc.createElement(ITEM_ELEMENT);
          itemElement.appendChild(createSimpleElement(doc, PROCESS_NAME_FILTER_ELEMENT, filter.getMask()));
          itemElement.appendChild(createSimpleElement(doc, TYPE_ELEMENT, DENY_PROCESS_FILTER_VAL));
          processFiltersElement.appendChild(itemElement);
        }
      }
    }

    argumentScopeElement.appendChild(processFiltersElement);
    argumentElement.appendChild(argumentScopeElement);

    final Element infoElement = doc.createElement(INFO_ELEMENT);
    infoElement.setAttribute(TYPE_ATTR, TYPE_PERFORMANCE_INFO);
    final String measureTypeStr = myParametersService.tryGetRunnerParameter(Constants.MEASURE_TYPE_VAR);
    MeasureType curMeasureType = MeasureType.SAMPLING;
    for(MeasureType measureType: MeasureType.values()) {
      if(measureType.getValue().equalsIgnoreCase(measureTypeStr)) {
        curMeasureType = measureType;
        break;
      }
    }

    if(!StringUtil.isEmptyOrSpaces(curMeasureType.getId())) {
      infoElement.appendChild(createSimpleElement(doc, MEASURE_TYPE_ELEMENT, curMeasureType.getId()));
    }

    infoElement.appendChild(createSimpleElement(doc, METER_KIND_ELEMENT, METER_KIND_RDTSC));

    final Element injectInfoElement = doc.createElement(INJECT_INFO_ELEMENT);
    final Element symbolSearchElement = doc.createElement(SYMBOL_SEARCH_ELEMENT);
    symbolSearchElement.appendChild(createSimpleElement(doc, SEARCH_PATHS_ELEMENT, ""));
    injectInfoElement.appendChild(symbolSearchElement);
    final Element injectInfoScopeElement = doc.createElement(SCOPE_ELEMENT);
    injectInfoScopeElement.appendChild(createSimpleElement(doc, PATTERN_FILTERS_ELEMENT, ""));
    injectInfoScopeElement.appendChild(createSimpleElement(doc, DENY_ATTRIBUTE_FILTERS_ELEMENT, ""));
    injectInfoElement.appendChild(injectInfoScopeElement);
    infoElement.appendChild(injectInfoElement);
    rootElement.appendChild(infoElement);

    doc.appendChild(rootElement);
    return myDocumentManager.convertDocumentToString(doc, outDocumentProperties);
  }

  @NotNull
  private static Element createSimpleElement(@NotNull final Document doc, @NotNull final String name, @NotNull final String value) {
    Element executableElement = doc.createElement(name);
    executableElement.setTextContent(value);
    return executableElement;
  }
}