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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * WebBox is a small layout tool used in servlet environment
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @since v1.0.0
 */
public class WebBox {
	public static final String JWEBBOXID = "WEBBOX";

	/** Optional, you can give a name to HtmlBox instance */
	private String name;

	/** A static method to prepare data, first be called if have */
	private String prepareStaticMethod;

	/** A Bean has a "prepare" methods to prepare data, 2nd called */
	private Object prepareBean;

	/** If Bean has prepareBeanMethod, use this one instead of "prepare" */
	private String prepareBeanMethod;

	/** A URL, 3rd called, if have */
	private String prepareURL;

	/** A text , 4th output, if not empty */
	private String text;

	/**
	 * A JSP or FTL page String, or a HtmlBox instance, or a HtmlBox class, 5th
	 * output, if not empty
	 */
	private Object page;

	/** Inside use hashmap store attributes */
	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	/** Point to father HtmlBox instance if have */
	private WebBox fatherHtmlBox;

	private HttpServletRequest request;

	private HttpServletResponse response;

	public WebBox() {
		// default constructor
	}

	/**
	 * Create a HtmlBox
	 * 
	 * @param page
	 *            The JSP or FTL or any URL, for example: "/template/abc.jsp"
	 */
	public WebBox(String page) {
		this.setPage(page);
	}

	/** Check if String null or empty */
	public static boolean isEmptyStr(String str) {
		return (str == null || str.length() == 0);
	}

	/** For subclasses override this method to do something */
	public void beforeExecute() {// NOSONAR
	}

	/** For subclasses override this method to do something */
	public void execute() {// NOSONAR
	}

	/** For subclasses override this method to do something */
	public void afterExecute() {// NOSONAR
	}

	/** For subclasses override this method to do something */
	public void afterPrepared() {// NOSONAR
	}

	/** For subclasses override this method to do something */
	public void afterShow() {// NOSONAR
	}

	/** Prepare data and out put text include page if have */
	public WebBox show(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.request = request;
			this.response = response;
			beforeExecute();
			execute();
			afterExecute();
			prepareOnly(request, response);
			afterPrepared();
			showText(request, response);
			showPageOrUrl(request, response, this.page, this);
			afterShow();
		} finally {
			this.request = null;
			this.response = null;
		}
		return this;
	}

	/** Direct out put the text into pageContext.out */
	private WebBox showText(HttpServletRequest request, HttpServletResponse response) {
		if (text != null && text.length() > 0)
			try {
				response.getWriter().write(text);
			} catch (IOException e) {
				throw new WebBoxException(e);
			}
		return this;
	}

	/** Prepare data, only but do not output text and do not show page */
	public void prepareOnly(HttpServletRequest request, HttpServletResponse response) {
		if (!isEmptyStr(prepareStaticMethod)) {
			int index = prepareStaticMethod.lastIndexOf('.');
			String className = prepareStaticMethod.substring(0, index);
			String methodName = prepareStaticMethod.substring(index + 1, prepareStaticMethod.length());
			if (isEmptyStr(className) || isEmptyStr(methodName))
				throw new WebBoxException("Error#001: Can not call method: " + prepareStaticMethod);
			try {
				Class<?> c = Class.forName(className);
				Method m = c.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, WebBox.class);
				m.invoke(c, request, response, this); // Call a static method
			} catch (Exception e) {
				throw new WebBoxException(e);
			}
		}
		if (prepareBean != null)
			executeBeanMethod(request, response);
		showPageOrUrl(request, response, this.prepareURL, this);
	}

	/** Execute Bean Method */
	private void executeBeanMethod(HttpServletRequest request, HttpServletResponse response) {
		try {
			Class<?> c = prepareBean.getClass();
			String methodName = isEmptyStr(prepareBeanMethod) ? "prepare" : prepareBeanMethod;
			Method m = c.getMethod(methodName, PageContext.class, WebBox.class);
			m.invoke(prepareBean, request, response, this); // Call a bean method
		} catch (Exception e) {
			throw new WebBoxException(e);
		}
	}

	/** Show page only, do not call prepareStaticMethod and URL */
	public WebBox showPageOnly(HttpServletRequest request, HttpServletResponse response) {
		return showPageOrUrl(request, response, this.page, this);
	}

	/**
	 * For subclasses override this method to do customized render
	 * 
	 * @throws Exception
	 */
	public void render(HttpServletRequest request, HttpServletResponse response, String pageOrUrl) throws Exception {// NOSONAR
		WebBoxRenders.renderAsHtml(request, response, pageOrUrl);
	}

	/** Private method, use RequestDispatcher to show a URL or JSP page */
	private static WebBox showPageOrUrl(HttpServletRequest request, HttpServletResponse response, Object page,
			WebBox currentBox) {
		if (page == null)
			return currentBox;
		if (page instanceof WebBox) {
			((WebBox) page).show(request, response);
			return currentBox;
		}
		if (!(page instanceof String))
			throw new WebBoxException("" + page + " is not a String or HtmlBox.");
		String pageOrUrl = (String) page;
		if (isEmptyStr(pageOrUrl))
			return currentBox;
		WebBox fatherHtmlBox = (WebBox) request.getAttribute(JWEBBOXID);
		if (fatherHtmlBox != null)
			currentBox.setFatherHtmlBox(fatherHtmlBox);
		request.setAttribute(JWEBBOXID, currentBox);
		try {
			response.getWriter().flush();
			currentBox.render(request, response, pageOrUrl);
		} catch (Exception e) {
			throw new WebBoxException(e);
		} finally {
			request.setAttribute(JWEBBOXID, fatherHtmlBox);
			currentBox.setFatherHtmlBox(null);
		}
		return currentBox;
	}

	/** Get current pageContext's HtmlBox instance */
	public static WebBox getBox(HttpServletRequest request) {
		WebBox currentBox = (WebBox) request.getAttribute(JWEBBOXID);
		if (currentBox == null)
			throw new WebBoxException("Error#003: Can not find HtmlBox instance in pageContext");
		return currentBox;
	}

	/** Get an attribute from current page's HtmlBox instance */
	public static <T> T getAttribute(HttpServletRequest request, HttpServletResponse response, String attributeName) {
		return getBox(request).getAttribute(attributeName); 
	}

	/** Assume the value is String or HtmlBox instance, show it */
	public static void showAttribute(HttpServletRequest request, HttpServletResponse response, String attributeName) {
		Object obj = WebBox.getAttribute(request, response, attributeName);
		showTarget(request, response, obj);
	}

	/**
	 * Show an target object, target can be: HtmlBox instance or String or List of
	 * HtmlBox instance or String
	 */
	public static void showTarget(HttpServletRequest request, HttpServletResponse response, Object target) {
		if (target == null)
			return;
		if (target instanceof WebBox)
			((WebBox) target).show(request, response);
		else if (target instanceof ArrayList<?>) {
			for (Object item : (ArrayList<?>) target)
				showTarget(request, response, item);
		} else if (target instanceof Class) {
			WebBox bx = null;
			try {
				bx = (WebBox) ((Class<?>) target).newInstance();
			} catch (Exception e) {
				throw new WebBoxException("Can not create HtmlBox instance for target class '" + target + "'", e);
			}
			bx.show(request, response);
		} else if (target instanceof String) {
			String str = (String) target;
			if (str.startsWith("/")) {
				showPageOrUrl(request, response, str, getBox(request));
			} else {
				try {
					response.getWriter().write(str);
				} catch (IOException e) {
					throw new WebBoxException(e);
				}
			}
		} else
			throw new WebBoxException("Can not show unknow type object " + target + " on page");
	}

	// Getter & Setters

	/** Set attribute for current HtmlBox instance */
	public WebBox setAttribute(String key, Object value) {
		attributeMap.put(key, value);
		return this;
	}

	/** Get HtmlBox's attribute */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		Object obj = attributeMap.get(key);
		if (obj == null && request != null) {
			obj = request.getAttribute(key);
			if (obj == null)
				obj = request.getParameter(key);
			if (obj == null)
				obj = request.getAttribute(key);
			if (obj == null)
				obj = request.getSession().getAttribute(key);
		}
		return (T) obj;
	}

	/**
	 * Search and return an attribute object follow this order:
	 * pageContext->request->parameter->session
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(String key) {
		if (request == null || key == null)
			return null;
		Object obj = request.getAttribute(key);
		if (obj == null)
			obj = request.getAttribute(key);
		if (obj == null)
			obj = request.getParameter(key);
		if (obj == null)
			obj = request.getSession().getAttribute(key);

		return (T) obj;
	}

	/** Set a request attribute */
	public void setRequestAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	/** Set a session attribute */
	public void setSessionAttribute(String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

	/** Get the prepare URL */
	public String getPrepareURL() {
		return prepareURL;
	}

	/**
	 * Set prepare URL, this URL be called after prepare methods but before show
	 * page
	 */
	public WebBox setPrepareURL(String prepareURL) {
		this.prepareURL = prepareURL;
		return this;
	}

	/** Get the page */
	public Object getPage() {
		return page;
	}

	/** Set a JSP page or URL */
	public WebBox setPage(Object page) {
		if (!(page == null || page instanceof String || page instanceof WebBox))
			throw new WebBoxException("setPage method only accept String or HtmlBox instance type parameter");
		this.page = page;
		return this;
	}

	/** Get the Text */
	public String getText() {
		return text;
	}

	/** Set the text String */
	public WebBox setText(String text) {
		this.text = text;
		return this;
	}

	/** Set a prepare static method */
	public WebBox setPrepareStaticMethod(String prepareStaticMethod) {
		this.prepareStaticMethod = prepareStaticMethod;
		return this;
	}

	/** Get the Prepare static method name */
	public String getPrepareStaticMethod() {
		return prepareStaticMethod;
	}

	/** Get the prepare bean instance */
	public Object getPrepareBean() {
		return prepareBean;
	}

	/** Set a prepare bean which has a prepare method */
	public WebBox setPrepareBean(Object prepareBean) {
		this.prepareBean = prepareBean;
		return this;
	}

	/** Get the prepare bean method name */
	public String getPrepareBeanMethod() {
		return prepareBeanMethod;
	}

	/** Set the bean prepare method name */
	public WebBox setPrepareBeanMethod(String prepareBeanMethod) {
		this.prepareBeanMethod = prepareBeanMethod;
		return this;
	}

	/** Get the attribute map of HtmlBox instance */
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	/** Set the attribute map for HtmlBox instance */
	public WebBox setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
		return this;
	}

	/** get the name of the HtmlBox instance */
	public String getName() {
		return name;
	}

	/** Set the name of the HtmlBox instance */
	public WebBox setName(String name) {
		this.name = name;
		return this;
	}

	/** Set the father page's HtmlBox instance */
	public WebBox getFatherHtmlBox() {
		return fatherHtmlBox;
	}

	/** Get the father page's HtmlBox instance */
	public void setFatherHtmlBox(WebBox fatherHtmlBox) {
		this.fatherHtmlBox = fatherHtmlBox;
	}

	/** Get current Box's HttpServletRequest if have */
	public HttpServletRequest getRequest() {
		return request;
	}

	/** Set HttpServletRequest to current Box */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/** Get current Box's HttpServletResponse if have */
	public HttpServletResponse getResponse() {
		return response;
	}

	/** Set HttpServletResponse to current Box */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/** A runtime exception caused by HtmlBox */
	public static class WebBoxException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public WebBoxException(String msg) {
			super(msg);
		}

		public WebBoxException(Throwable e) {
			super(e);
		}

		public WebBoxException(String msg, Throwable e) {
			super(msg, e);
		}
	}

}
