<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<title></title>
</head>
<body>
    <div>现在加载的是&nbsp${Global['number']}.htm&nbsp页面</div>
    <!--index.htm动态翻译成index_htm.java后加载运行-->
    <!--运行状态下index.htm内容更改则重新翻译编译加载运行-->
    <jsp:include page="/data/staticize/page/index.htm"></jsp:include>
    <!--这样支持伪静态URL的真静态化页面-->
    <!--虽然每次都更换加载的静态页面，但静态页面不会重复翻译编译加载。静态页面更改除外-->
    <jsp:include page="/data/staticize/page/${Global['number']}.htm"></jsp:include>

    <!--另一种实现方式：在service判断存在并可跳转至静态页面时直接内部跳转至静态页面URL-->
    <!--这时页头，页脚，动态部分（用户状态）生成就是问题-->
    <div>${Global['timestamp']}</div>
</body>
</html>
