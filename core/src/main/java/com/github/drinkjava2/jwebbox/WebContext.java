/*
 * Copyright (C) 2016-2020 Yong Zhu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.github.drinkjava2.jwebbox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

/**
 * WebContext is the wrap of servletContext (=request + response) or JSP
 * pageContext context, it has 2 constructor:
 * 
 * WebContext(PageContext) <br/>
 * WebContext(HttpServletRequest, HttpServletResponse); <br/>
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @Since 2.1.3
 */
public class WebContext {
	private boolean jsp = false;
	private Map<String, Object> pageContextAttrMap;

	private HttpServletRequest request;
	private HttpServletResponse response;
	PageContext pageContext;

	public WebContext(PageContext pageContext) {
		WebBoxException.assureNotNull(pageContext, "Can not use null page Context to build WebContext");
		jsp = true;
		this.pageContext = pageContext;
		this.request = (HttpServletRequest) pageContext.getRequest();
		this.response = (HttpServletResponse) pageContext.getResponse();
	}

	public WebContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpSession getSession() {
		return request.getSession();
	}

	public void print(String text) {
		try {
			if (isJsp())
				pageContext.getOut().print(text);
			else
				response.getWriter().print(text);
		} catch (IOException e) {
			throw new WebBoxException(e);
		}
	}

	public void flush() {
		try {
			if (isJsp())
				pageContext.getOut().flush();
			else
				response.getWriter().flush();
		} catch (IOException e) {
			throw new WebBoxException(e);
		}
	}

	public void include(WebContext context, String pageOrUrl) throws ServletException, IOException {
//		flush();
//		getRequest().getRequestDispatcher(pageOrUrl).include(context.getRequest(), context.getResponse());
//		
		System.out.println("in  include");
		pageContext.getOut().flush();
		pageContext.getRequest().getRequestDispatcher(pageOrUrl).include(pageContext.getRequest(),
				pageContext.getResponse());
		System.out.println("done  include");
	}

	public void setPageContextAttribute(String key, Object value) {// FOR JSP ONLY
		if (isJsp())
			pageContext.setAttribute(key, value);
		else {
			if (pageContextAttrMap == null)
				pageContextAttrMap = new HashMap<>();
			pageContextAttrMap.put(key, value);
		}
	}

	public Object getPageContextAttribute(String key) {// FOR JSP ONLY
		if (isJsp())
			return pageContext.getAttribute(key);
		else {
			if (pageContextAttrMap == null)
				return null;
			return pageContextAttrMap.get(key);
		}
	}
	
	public PageContext getPageContext() {// FOR JSP ONLY
		return pageContext;
	}

	// =====getter && setters======
	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public boolean isJsp() {
		return jsp;
	}
}
