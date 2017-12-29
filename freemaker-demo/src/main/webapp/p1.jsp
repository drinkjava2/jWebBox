<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<br/>
This is p1 first line <br/>
<%
//response.flushBuffer();
javax.servlet.jsp.PageContext pageContext2 = javax.servlet.jsp.JspFactory.getDefaultFactory().getPageContext(this, request, response,
			null, true, 8192, true); 
 pageContext2.getOut().flush();
 new WebBox("/p2.jsp").show();
%>
This is p1 second line <br/>
<br/>