package jetbrains.buildServer.dotTrace.agent;

import java.util.HashMap;
import java.util.Map;
import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineArgumentsService;
import jetbrains.buildServer.dotNet.buildRunner.agent.FileService;
import jetbrains.buildServer.dotNet.buildRunner.agent.ResourceGenerator;
import jetbrains.buildServer.dotNet.buildRunner.agent.XmlDocumentManager;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProjectGenerator implements ResourceGenerator<Context> {
  private static final Map<String, String> outDocumentProperties = new HashMap<String, String>();
  private static final String ROOT_ELEMENT = "root";
  private static final String TYPE_ATTR = "type";
  private static final String HOST_PARAMETERS_ELEMENT = "HostParameters";
  private static final String TYPE_LOCAL_HOST_PARAMETERS = "LocalHostParameters";
  private static final String ARGUMENT_ELEMENT = "Argument";
  private static final String ARGUMENTS_ELEMENT = "Arguments";
  private static final String FILE_NAME_ELEMENT = "FileName";
  private static final String PROFILE_CHILD_PROCESSES_ELEMENT = "ProfileChildProcesses";
  private static final String TRUE_VAL = "True";
  private static final String WORKING_DIRECTORY_ELEMENT = "WorkingDirectory";
  private static final String INFO_ELEMENT = "Info";
  private static final String MEASURE_TYPE_ELEMENT = "MeasureType";
  private static final String MEASURE_TYPE_SAMPLING = "Sampling";
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

  private final XmlDocumentManager myDocumentManager;
  private final CommandLineArgumentsService myCommandLineArgumentsService;
  private final FileService myFileService;

  public ProjectGenerator(
    @NotNull final XmlDocumentManager documentManager,
    @NotNull final CommandLineArgumentsService commandLineArgumentsService,
    @NotNull final FileService fileService) {
    myDocumentManager = documentManager;
    myCommandLineArgumentsService = commandLineArgumentsService;
    myFileService = fileService;
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
    argumentElement.appendChild(createSimpleElement(doc, PROFILE_CHILD_PROCESSES_ELEMENT, TRUE_VAL));
    argumentElement.appendChild(createSimpleElement(doc, WORKING_DIRECTORY_ELEMENT, myFileService.getCheckoutDirectory().getPath()));
    rootElement.appendChild(argumentElement);

    final Element argumentScopeElement = doc.createElement(SCOPE_ELEMENT);
    final Element processFiltersElement = doc.createElement(PROCESS_FILTERS_ELEMENT);
    processFiltersElement.appendChild(createSimpleElement(doc, ITEM_ELEMENT, ""));
    argumentScopeElement.appendChild(processFiltersElement);
    argumentElement.appendChild(argumentScopeElement);

    final Element infoElement = doc.createElement(INFO_ELEMENT);
    infoElement.setAttribute(TYPE_ATTR, TYPE_PERFORMANCE_INFO);
    infoElement.appendChild(createSimpleElement(doc, MEASURE_TYPE_ELEMENT, MEASURE_TYPE_SAMPLING));
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