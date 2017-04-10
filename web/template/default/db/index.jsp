<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
</head>
<body>
  <div>version:${Global["version"]}</div>
  <div>table:${Global["table"]}</div>
  <div>command:${Global["command"]}</div>
  <div>AssociativeArray:${Global["AssociativeArray"]}</div>
  <c:forEach items="${Global['AssociativeArray']}" var="m">
    键:${m.key} <br>
    值:${m.value} <br>
  </c:forEach>
</body>
</html>
