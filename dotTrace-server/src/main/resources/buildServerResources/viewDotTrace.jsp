<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2000-2023 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

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