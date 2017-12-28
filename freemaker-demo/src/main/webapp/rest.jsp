<%@page import="com.github.drinkjava2.jwebboxdemo.DemoBoxConfig.*"%>
<%
	String uri = request.getRequestURI();
	if (uri.contains("/demo/2"))
		new DemoTopDown().show();
	else if (uri.contains("/demo/3"))
		new DemoPrepareData().show();
	else if (uri.contains("/demo/4"))
		new DemoList().show();
	else if (uri.contains("/demo/5"))
		new DemoTable().show();
	else
		new DemoHomePage().show();
%>