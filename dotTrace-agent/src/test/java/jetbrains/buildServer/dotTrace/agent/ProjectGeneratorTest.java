package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static org.assertj.core.api.BDDAssertions.then;

public class ProjectGeneratorTest {
  private static final XmlDocumentManager ourDocManager = new XmlDocumentManagerImpl();
  private Mockery myCtx;
  private XmlDocumentManager myXmlDocumentManager;
  private FileService myFileService;
  private CommandLineArgumentsService myCommandLineArgumentsService;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();
    myFileService = myCtx.mock(FileService.class);
    myXmlDocumentManager = myCtx.mock(XmlDocumentManager.class);
    myCommandLineArgumentsService = myCtx.mock(CommandLineArgumentsService.class);
  }

  @Test
  public void shouldGenerateContent() {
    // Given
    String expectedContent = "<root>" +
                             "<HostParameters type=\"LocalHostParameters\"/>" +
                             "<Argument type=\"StandaloneArgument\">" +
                             "<Arguments>arg1 arg2</Arguments>" +
                             "<FileName>wd" + File.separator + "tool</FileName>" +
                             "<ProfileChildProcesses>True</ProfileChildProcesses>" +
                             "<WorkingDirectory>wd</WorkingDirectory>" +
                             "<Scope>" +
                             "<ProcessFilters>" +
                             "<Item/>" +
                             // "<Item>" +
                             // "<ProcessNameFilter>nunit-console*</ProcessNameFilter>" +
                             // "<Type>Deny</Type>" +
                             // "</Item>" +
                             "</ProcessFilters>" +
                             "</Scope>" +
                             "</Argument>" +
                             "<Info type=\"PerformanceInfo\">" +
                             "<MeasureType>Sampling</MeasureType>" +
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
      will(Expectations.returnValue(ourDocManager.createDocument()));

      //noinspection unchecked
      oneOf(myXmlDocumentManager).convertDocumentToString(with(Expectations.any(Document.class)), with(Expectations.any(Map.class)));
      will(new CustomAction("doc") {
        @Override
        public Object invoke(Invocation invocation) throws Throwable {
          //noinspection unchecked
          return ourDocManager.convertDocumentToString((Document)invocation.getParameter(0), (Map<String, String>)invocation.getParameter(1));
        }
      });

      oneOf(myCommandLineArgumentsService).createCommandLineString(setup.getArgs());
      will(Expectations.returnValue("arg1 arg2"));

      oneOf(myFileService).getCheckoutDirectory();
      will(Expectations.returnValue(new File("wd")));
    }});

    final ProjectGenerator instance = createInstance();

    // When
    final String content = instance.create(new Context(setup, new File("a"), new File("b"), new File("c"), new File("s")));

    // Then
    myCtx.assertIsSatisfied();
    then(content.trim().replace("\n", "").replace("\r", "")).isEqualTo(expectedContent.trim().replace("\n", "").replace("\r", ""));
  }

  @NotNull
  private ProjectGenerator createInstance()
  {
    return new ProjectGenerator(
      myXmlDocumentManager,
      myCommandLineArgumentsService,
      myFileService);
  }
}
