<%@page import="com.github.drinkjava2.jwebboxdemo.DemoBoxConfig.*"%><%
  String uri = request.getRequestURI();
	if ("/demo/2".equals(uri))
		new DemoTopDown().show(pageContext);
	else if ("/demo/3".equals(uri))
		new DemoPrepareData().show(pageContext);
	else if ("/demo/4".equals(uri))
		new DemoList().show(pageContext);
	else if ("/demo/5".equals(uri))
		new DemoTable().show(pageContext);	
	else
		new DemoHomePage().show(pageContext); 
%>