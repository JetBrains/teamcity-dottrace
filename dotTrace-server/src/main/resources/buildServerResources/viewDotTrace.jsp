<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="bean" class="jetbrains.buildServer.dotTrace.server.DotTraceBean"/>

<div class="parameter">
  Use dotTrace: <props:displayValue name="${bean.useDotTraceKey}" emptyValue="false"/>
</div>

<div class="parameter">
  Path to dotTrace: <props:displayValue name="${bean.pathKey}" emptyValue="<empty>"/>
</div>
