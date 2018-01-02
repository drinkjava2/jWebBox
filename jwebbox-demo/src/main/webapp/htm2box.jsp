<%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.github.drinkjava2.jwebbox.WebBox"%><%
	String uri=StringUtils.substringBefore(request.getRequestURI(),".");
	  uri = StringUtils.substringAfterLast(uri, "/");
	if (uri == null || uri.length() == 0)
		uri = "demo1";
	WebBox box = (WebBox) Class.forName("com.github.drinkjava2.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
	box.show(pageContext);
%>