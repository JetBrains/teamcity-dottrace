

package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import jetbrains.buildServer.dotTrace.MeasureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static org.assertj.core.api.BDDAssertions.then;

public class ProjectGeneratorTest {
  private static final String ourlineSeparator = System.getProperty("line.separator");
  private static final XmlDocumentManager ourDocManager = new XmlDocumentManagerImpl();
  private Mockery myCtx;
  private XmlDocumentManager myXmlDocumentManager;
  private FileService myFileService;
  private CommandLineArgumentsService myCommandLineArgumentsService;
  private TextParser<List<ProcessFilter>> myProcessFiltersParser;
  private RunnerParametersService myRunnerParametersService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();
    //noinspection unchecked
    myProcessFiltersParser = (TextParser<List<ProcessFilter>>)myCtx.mock(TextParser.class);
    myFileService = myCtx.mock(FileService.class);
    myXmlDocumentManager = myCtx.mock(XmlDocumentManager.class);
    myCommandLineArgumentsService = myCtx.mock(CommandLineArgumentsService.class);
    myRunnerParametersService = myCtx.mock(RunnerParametersService.class);
  }

  @DataProvider(name = "generateContentCases")
  public Object[][] getGenerateContentCases() {
    return new Object[][] {
      { "True", "True", null, Collections.emptyList(), "", null},
      { "False", "False", null, Collections.emptyList(), "", null},
      { "", "True", null, Collections.emptyList(), "" , null},
      { null, "True", null, Collections.emptyList(), "", null},
      { "True", "True", "nunit-console*", Arrays.asList(new ProcessFilter("nunit-console*")), "<Item/><Item><ProcessNameFilter>nunit-console*</ProcessNameFilter><Type>Deny</Type></Item>", null},
      { "True", "True", "nunit-console*" + ourlineSeparator + "Abc", Arrays.asList(new ProcessFilter("nunit-console*"), new ProcessFilter("Abc")), "<Item/><Item><ProcessNameFilter>nunit-console*</ProcessNameFilter><Type>Deny</Type></Item><Item><ProcessNameFilter>Abc</ProcessNameFilter><Type>Deny</Type></Item>", null},
    };
  }

  @Test(dataProvider = "generateContentCases")
  public void shouldGenerateContent(
    @Nullable final String profileChildProcesses,
    @NotNull final String profileChildProcessesXmlValue,
    @Nullable final String processFilters,
    @NotNull final List<ProcessFilter> processFiltersList,
    @NotNull final String processFiltersXmlValue,
    @Nullable MeasureType measureType) {
    // Given
    if(measureType == null) {
      measureType = MeasureType.SAMPLING;
    }

    final MeasureType curMeasureType = measureType;

    String expectedContent = "<root>" +
                             "<HostParameters type=\"LocalHostParameters\"/>" +
                             "<Argument type=\"StandaloneArgument\">" +
                             "<Arguments>arg1 arg2</Arguments>" +
                             "<FileName>wd" + File.separator + "tool</FileName>" +
                             "<ProfileChildProcesses>" + profileChildProcessesXmlValue + "</ProfileChildProcesses>" +
                             "<WorkingDirectory>wd</WorkingDirectory>" +
                             "<Scope>" +
                             (!processFiltersXmlValue.equals("") ? "<ProcessFilters>" + processFiltersXmlValue + "</ProcessFilters>" : "<ProcessFilters/>") +
                             "</Scope>" +
                             "</Argument>" +
                             "<Info type=\"PerformanceInfo\">" +
                             "<MeasureType>" + curMeasureType.getDescription() + "</MeasureType>" +
                             "<MeterKind>Rdtsc</MeterKind>" +
                             "<InjectInfo>" +
                             "<SymbolSearch>" +
                             "<SearchPaths/>" +
                             "</SymbolSearch>" +
                             "<Scope>" +
                             "<PatternFilters/>" +
                             "<DenyAttributeFilters/>" +
                             "</Scope>" +
                             "</InjectInfo>" +
                             "</Info>" +
                             "</root>";

    final CommandLineSetup setup = new CommandLineSetup(
      "wd" + File.separator + "tool",
      Arrays.asList(
        new CommandLineArgument("arg1", CommandLineArgument.Type.PARAMETER),
        new CommandLineArgument("arg2", CommandLineArgument.Type.PARAMETER)),
      Collections.<CommandLineResource>emptyList());

    myCtx.checking(new Expectations() {{
      oneOf(myXmlDocumentManager).createDocument();
      will(returnValue(ourDocManager.createDocument()));

      //noinspection unchecked
      oneOf(myXmlDocumentManager).convertDocumentToString(with(any(Document.class)), with(any(Map.class)));
      will(new CustomAction("doc") {
        @Override
        public Object invoke(Invocation invocation) throws Throwable {
          //noinspection unchecked
          return ourDocManager.convertDocumentToString((Document)invocation.getParameter(0), (Map<String, String>)invocation.getParameter(1));
        }
      });

      oneOf(myCommandLineArgumentsService).createCommandLineString(setup.getArgs());
      will(returnValue("arg1 arg2"));

      oneOf(myFileService).getCheckoutDirectory();
      will(returnValue(new File("wd")));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.PROFILE_CHILD_PROCESSES_VAR);
      will(returnValue(profileChildProcesses));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.PROCESS_FILTERS_VAR);
      will(returnValue(processFilters));

      //noinspection ConstantConditions
      allowing(myProcessFiltersParser).parse(processFilters);
      will(returnValue(processFiltersList));

      oneOf(myRunnerParametersService).tryGetRunnerParameter(Constants.MEASURE_TYPE_VAR);
      will(returnValue(curMeasureType.getValue()));
    }});

    final ProjectGenerator instance = createInstance();

    // When
    final String content = instance.create(new Context(setup, new File("a"), new File("b"), new File("c"), new File("s")));

    // Then
    myCtx.assertIsSatisfied();
    then(content.trim().replace("\n", "").replace("\r", "").replace(" ", "")).isEqualTo(expectedContent.trim().replace("\n", "").replace("\r", "").replace(" ", ""));
  }

  @NotNull
  private ProjectGenerator createInstance()
  {
    return new ProjectGenerator(
      myProcessFiltersParser,
      myXmlDocumentManager,
      myCommandLineArgumentsService,
      myFileService,
      myRunnerParametersService);
  }
}