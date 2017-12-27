<%@page import="com.github.drinkjava2.jwebboxdemo.DemoBoxConfig.*"%>
<%
	String uri = request.getRequestURI();
System.out.println(uri);
	if (uri.contains("/demo/1"))
		new DemoTopDown().show(pageContext);
	if (uri.contains("/demo/2"))
		new DemoTopDown().show(pageContext);
	if (uri.contains("/demo/3"))
		new DemoPrepareData().show(pageContext);
	if (uri.contains("/demo/4"))
		new DemoList().show(pageContext);
	if (uri.contains("/demo/5"))
		new DemoTable().show(pageContext);
	else
		new DemoHomePage().show(pageContext);
%>