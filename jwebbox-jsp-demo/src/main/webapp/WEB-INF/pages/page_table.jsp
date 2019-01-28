<%@page import="java.util.List"%><%@page import="com.github.drinkjava2.jwebbox.JspBox"%>
<%
JspBox box = JspBox.getBox(pageContext);
	List<String> itemList = box.getAttribute("itemList");
	int row = (Integer) box.getAttribute("row");
	int col = (Integer) box.getAttribute("col");
	int index=0;
	JspBox render = (JspBox) box.getAttribute("render");
%>
<table>
	<%
		for (int i = 1; i <= row; i++) {
			out.write("<tr>");
			for (int j = 1; j <= col; j++) {
				out.write("<td>");
				if(index<itemList.size()){
				  render.setAttribute("item", itemList.get(index++)); 
				  render.show(pageContext); 
				}
				out.write("<td/>");
			}
			out.write("<tr/>");
		}
	%>
</table>
