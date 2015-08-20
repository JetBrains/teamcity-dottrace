package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BuildPublisherTest {
  private static final String ourlineSeparator = System.getProperty("line.separator");
  private Mockery myCtx;
  private FileService myFileService;
  private TextParser<Thresholds> myReportParser;
  private XmlDocumentManager myXmlDocumentManager;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();

    //noinspection unchecked
    myReportParser = (TextParser<Thresholds>)myCtx.mock(TextParser.class);
    myFileService = myCtx.mock(FileService.class);
    myXmlDocumentManager = myCtx.mock(XmlDocumentManager.class);
  }

  @Test
  public void shouldPublishAfterBuildArtifactFile() {
    // Given
    final File reportFile = new File("report");
    final Thresholds thresholds = new Thresholds(Arrays.asList(new Threshold("Method1", "100", "1000")));

    myCtx.checking(new Expectations() {{
      //noinspection EmptyCatchBlock
      try {
        oneOf(myFileService).readAllTextFile(reportFile);
      }
      catch (IOException e) {
      }

      will(returnValue("report's content"));

      oneOf(myReportParser).parse("report's content");
      will(returnValue(thresholds));
    }});

    final ResourcePublisher instance = createInstance();

    // When
    instance.publishAfterBuildArtifactFile(new CommandLineExecutionContext(0), reportFile);

    // Then
    myCtx.assertIsSatisfied();
  }

  @NotNull
  private ResourcePublisher createInstance()
  {
    return new BuildPublisher(
      myReportParser,
      myFileService,
      myXmlDocumentManager);
  }
}
