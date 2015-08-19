package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import java.io.IOException;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import org.jetbrains.annotations.NotNull;

public class BuildPublisher implements ResourcePublisher {
  private final TextParser<Thresholds> myReportParser;
  private final FileService myFileService;
  private final XmlDocumentManager myDocumentManager;

  public BuildPublisher(
    @NotNull final TextParser<Thresholds> reportParser,
    @NotNull final FileService fileService,
    @NotNull final XmlDocumentManager documentManager) {
    myReportParser = reportParser;
    myFileService = fileService;
    myDocumentManager = documentManager;
  }

  @Override
  public void publishBeforeBuildFile(@NotNull final CommandLineExecutionContext commandLineExecutionContext, @NotNull final File file, @NotNull final String content) {
  }

  @Override
  public void publishAfterBuildArtifactFile(@NotNull final CommandLineExecutionContext commandLineExecutionContext, @NotNull final File reportFile) {
    try {
      final String reportContent = myFileService.readAllTextFile(reportFile);
      final Thresholds reportResults = myReportParser.parse(reportContent);
    }
    catch (IOException e) {
      new BuildException(e.getMessage());
    }
  }
}