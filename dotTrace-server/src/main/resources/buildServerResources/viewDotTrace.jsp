<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="bean" class="jetbrains.buildServer.dotTrace.server.DotTraceBean"/>

<div class="parameter">
  Run build step under dotTrace profiler: <props:displayValue name="${bean.useDotTraceKey}" emptyValue="false"/>
</div>

<div class="parameter">
  Path to dotTrace ConsoleProfiler.exe: <props:displayValue name="${bean.pathKey}" emptyValue="<empty>"/>
</div>

<div class="parameter">
  Measure type: <props:displayValue name="${bean.measureTypeKey}" emptyValue="<empty>"/>
</div>

<div class="parameter">
  Profile child processes: <props:displayValue name="${bean.profileChildProcessesKey}" emptyValue="<empty>"/>
</div>

<div class="parameter">
  Filter processes: <props:displayValue name="${bean.processFiltersKey}" emptyValue="<empty>"/>
</div>

<div class="parameter">
  Path for storing performance snapshot: <props:displayValue name="${bean.snapshotsPathKey}" emptyValue="<empty>"/>
</div>

<div class="parameter">
  Threshold values: <props:displayValue name="${bean.thresholdsKey}" emptyValue="<empty>"/>
</div>