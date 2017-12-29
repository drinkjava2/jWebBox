<%@ taglib prefix="ex" uri="WEB-INF/custom.tld"%>
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<html>
<head>
<title>JWebBox2.1 Demo</title>
</head>
<body>
	<p>body start</p>

	<div>
		<%  out.flush();
			new WebBox("/p1.jsp").show();
		%>
	</div>

	<p>body end</p>
</body>
</html>
