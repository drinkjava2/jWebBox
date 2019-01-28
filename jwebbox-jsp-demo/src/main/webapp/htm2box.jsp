<%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.github.drinkjava2.jwebbox.JspBox"%><%
	String uri=StringUtils.substringBefore(request.getRequestURI(),".");
	  uri = StringUtils.substringAfterLast(uri, "/");
	if (uri == null || uri.length() == 0)
		uri = "demo1";
	JspBox box = (JspBox) Class.forName("com.github.drinkjava2.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
	box.show(pageContext);
%>