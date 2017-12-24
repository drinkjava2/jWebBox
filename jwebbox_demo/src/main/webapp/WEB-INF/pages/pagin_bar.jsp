<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>

<%!
@SuppressWarnings("all")
public static class ParameterUtil{ 
	public static Map getParameterMap(String parameterString) {
		HashMap m = new HashMap();
		if (WebBox.isEmptyStr(parameterString))
			return m;
		String[] arr = parameterString.split("&");
		for (int i = 0; i < arr.length; i++) {
			String string = arr[i];
			String[] arr2 = string.split("=");
			if (arr2 != null && arr2.length == 2 && !WebBox.isEmptyStr(arr2[0])) {
				m.put(arr2[0], arr2[1]);
			}
		}
		return m;
	}
	
	public static String getParameterString(Map parameterMap) {
		StringBuffer sb = new StringBuffer();
		Set set = parameterMap.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key_ = (String) iter.next();
			if (!WebBox.isEmptyStr((String) parameterMap.get(key_))) {
				if (sb.length() > 0)
					sb.append("&");
				sb.append(key_ + "=" + parameterMap.get(key_));
			}
		}
		return sb.toString();
	}
	
	public static String joinParameters(String parameterString1, String parameterString2) {
		Map m1 = getParameterMap(parameterString1);
		Map m2 = getParameterMap(parameterString2);
		m1.putAll(m2);
		return getParameterString(m1);
	} 
}

static String getPageLink(HttpServletRequest request, int pageNumber, String pageId) {
	String newQueryString = ParameterUtil.joinParameters(request.getQueryString(), pageId + "_pageNo=" + pageNumber);
	String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
	if (WebBox.isEmptyStr(uri))
		uri = request.getRequestURI();
	return  uri + "?" + newQueryString;
}
%>

<%
String paginId=WebBox.getAttribute(pageContext, "pageId");
int pageNo=(Integer)WebBox.getAttribute(pageContext, paginId+"_pageNo");  
int totalPage=(Integer)WebBox.getAttribute(pageContext, "totalPage");

	int N = 6;
	int from;
	int to;
 
	if (totalPage < N) {
		from = 1;
		to = totalPage;
	} else {
		int n1 = N / 2;
		int n2 = (N - 1) / 2;
		from = pageNo - n1;
		to = pageNo + n2;

		if (from < 1) {
			from = 1;
			to = from + N - 1;
		} else if (to > totalPage) {
			to = totalPage;
			from = to - N + 1;
		}
	}
%>

<div class="yahoo" style="float:right;padding-right:32px;">
<%
 	if (from > 1) {
 %> <A href="<%=getPageLink(request, 1, paginId)%>">First</A>
<%
	}
%>  <%
 	if (pageNo > 1) {
 %> <A href="<%=getPageLink(request, pageNo - 1, paginId)%>">Previous</A>
<%
	}
%> <%
 	for (int i = from; i <= to; i++) {
 		if (i == pageNo) {
 %> <%=i%> <%
 	} else {
 %> <A href="<%=getPageLink(request, i, paginId)%>"><%=i%></A>
<%
	}
	}
%> <%
 	if (pageNo < totalPage) {
 %> <A href="<%=getPageLink(request, pageNo + 1, paginId)%>">
Next</A> <%
 	}
 %><%
 	if (to < totalPage) {
 %> <A href="<%=getPageLink(request, totalPage, paginId)%>">Last</A>
<%
	}
%> 
</div>