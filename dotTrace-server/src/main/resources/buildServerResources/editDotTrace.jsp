<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags"  %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="bean" class="jetbrains.buildServer.dotTrace.server.DotTraceBean"/>

<l:settingsGroup title="dotTrace">
  <tr class="advancedSetting">
    <th><label for="${bean.useDotTraceKey}">Use dotTrace:</label></th>
    <td><props:checkboxProperty name="${bean.useDotTraceKey}" />
      <span class="smallNote">The tests will be started under the JetBrains dotTrace tool.</span>
      <span class="error" id="error_${bean.useDotTraceKey}"></span>
    </td>
  </tr>

  <tr class="advancedSetting">
    <th><label for="${bean.dotTracePathKey}">Path to dotTrace: <l:star/></label></th>
    <td>
      <div class="completionIconWrapper">
        <props:textProperty name="${bean.dotTracePathKey}" className="longField"/>
      </div>
      <span class="error" id="error_${bean.dotTracePathKey}"></span>
      <span class="smallNote">Specify path to dotTrace.exe</span>
    </td>
  </tr>

  <tr class="advancedSetting">
    <th><label for="com.dotTracePlugin.common.Thresholds">Threshold values: <l:star/></label></th>
    <td>
      <props:multilineProperty name="${bean.dotTraceThresholdsKey}" className="longField" cols="30" rows="10" expanded="true" linkTitle="Enter performance thresholds"/>
            <span class="smallNote">Newline-separated list of methods and their performance thresholds.
                <br/>Pattern: <b>Namespace.Class.Method TotalTime OwnTime</b>, where
                <br/><b>TotalTime</b> - execution time of the method's call subtree in ms.
                <br/><b>OwnTime</b> - method's own execution time in ms.
                <br/>To compare profiling results with previous successful builds, instead of the
                absolute time value, specify one of the following:
                <br/><b>F[tolerance]</b> - compare (time + tolerance) against the value from the <i>first</i> successful build.
                <br/><b>A[tolerance]</b> - compare (time + tolerance) against the <i>average</i> value calculated for all successful builds.
                <br/><b>L[tolerance]</b> - compare (time + tolerance) against the value from the <i>last</i> successful build.
                <br/><b>[tolerance]</b> is set in percent.
                <br/>Use the <b>0</b> value to ignore a certain parameter.
                <br/>
                <br/>E.g., the build step will fail if <b>Test1</b> total time exceeds 100 ms or its own time exceeds
                the own time from the first successful build by more than 15%:
                <br/><b>IntegrationTests.MainTests.Test1 100 F15</b>
                <br/>
            </span>
    </td>
  </tr>

</l:settingsGroup>