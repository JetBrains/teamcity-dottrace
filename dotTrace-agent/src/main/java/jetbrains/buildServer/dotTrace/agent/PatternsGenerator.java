package jetbrains.buildServer.dotTrace.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.dotTrace.Constants;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PatternsGenerator implements ResourceGenerator<Context> {
  private static final String PATTERNS_ELEMENT = "Patterns";
  private static final String PATTERN_ELEMENT = "Pattern";

  private final TextParser<Thresholds> myReportPatternsParser;
  private final RunnerParametersService myParametersService;
  private final XmlDocumentManager myDocumentManager;

  public PatternsGenerator(
    @NotNull final TextParser<Thresholds> reportPatternsParser,
    @NotNull final RunnerParametersService parametersService,
    @NotNull final XmlDocumentManager documentManager) {
    myReportPatternsParser = reportPatternsParser;
    myParametersService = parametersService;
    myDocumentManager = documentManager;
  }

  @NotNull
  @Override
  public String create(@NotNull final Context context) {
    final Document doc = myDocumentManager.createDocument();
    final Element patternsElement = doc.createElement(PATTERNS_ELEMENT);
    String thresholdsStr = myParametersService.tryGetRunnerParameter(Constants.THRESHOLDS_VAR);
    if(!StringUtil.isEmptyOrSpaces(thresholdsStr)) {
      final Thresholds thresholds = myReportPatternsParser.parse(thresholdsStr);
      for(Threshold threshold: thresholds.getThresholds()) {
        final Element patternElement = createSimpleElement(doc, PATTERN_ELEMENT, threshold.getMethodName());
        patternsElement.appendChild(patternElement);
      }
    }

    doc.appendChild(patternsElement);
    return myDocumentManager.convertDocumentToString(doc, Collections.<String, String>emptyMap());
  }

  @NotNull
  private static Element createSimpleElement(@NotNull final Document doc, @NotNull final String name, @NotNull final String value) {
    Element executableElement = doc.createElement(name);
    executableElement.setTextContent(value);
    return executableElement;
  }
}
