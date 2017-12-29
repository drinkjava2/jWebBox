<%@page import="java.util.ArrayList"%>
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<%
	ArrayList<WebBox> boxlist = WebBox.getAttribute(pageContext,"boxlist");
%>
<div id="temp_left" style="margin: 10px; width: 500px; float: left; background-color:#CCFFCC;">
	<%
		boxlist.get(0).show(pageContext);
	%>
</div>
<div id="temp_right"  style="margin: 10px; float: right; width: 350px;background-color:#FFFFCC;">
	<%
		boxlist.get(1).show(pageContext);
	%>
</div>