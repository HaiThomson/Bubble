<%@ page import="source.kernel.helper.MapHelper" %>
<%@ page import="java.util.Map" %>
<%
	Map ENV = (Map) request.getAttribute("ENV");
	Map Global = (Map) request.getAttribute("Global");
	Map allSession = (Map) Global.get("allsession");
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
	<title>show</title>
</head>
<body>
	<div>All Session:</div>
	<pre><%=MapHelper.mapToString(allSession)%></pre>
	<br>
	<div>ENV:</div>
	<pre><%=MapHelper.mapToString(ENV)%></pre>
	<br>
	<div>Global:</div>
	<pre><%=MapHelper.mapToString(Global)%></pre>
</body>
</html>
