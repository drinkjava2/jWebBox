<%@page import="com.github.drinkjava2.jwebboxdemo.DemoBoxConfig.*"%>
<%
	String uri = request.getRequestURI();
	if (uri.contains("/demo/2"))
		new DemoTopDown().show(pageContext);
	else if (uri.contains("/demo/3"))
		new DemoPrepareData().show(pageContext);
	else if (uri.contains("/demo/4"))
		new DemoList().show(pageContext);
	else if (uri.contains("/demo/5"))
		new DemoTable().show(pageContext);
	else
		new DemoHomePage().show(pageContext);
%>