<%@page import="java.util.ArrayList"%>
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<%
	ArrayList<WebBox> boxlist =  WebBox.getAttribute(pageContext,"boxlist");
%>
<div style="width: 880px; margin: auto; background-color: #CCFFCC;">
	<%
		boxlist.get(0).show(pageContext);
	%>
</div>
<div style="width: 880px; margin: auto; background-color: #FFFFCC;">
	<%
		boxlist.get(1).show(pageContext);
	%>
</div>