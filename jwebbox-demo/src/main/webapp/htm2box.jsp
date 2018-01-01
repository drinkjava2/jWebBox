<%@page import="com.github.drinkjava2.jwebbox.WebBox"%><%!
    /* Gets the String  between two Strings. Only the first match is returned.  */
	static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null)
			return null;
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1)
				return str.substring(start + open.length(), end);
		}
		return null;
	}%><%
	String uri = substringBetween(request.getRequestURI(), "/", ".htm");
	if (uri == null || uri.length() == 0)
		uri = "demo1";
	WebBox box = (WebBox) Class.forName("com.github.drinkjava2.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
	box.show(pageContext);
%>