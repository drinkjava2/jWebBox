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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

/**
 * JWebBox is a small layout tool used for JSP or HTML layout, playing the same
 * role like Apache Tiles and SiteMesh, no XML file, no Tags, simple, can be
 * used to support whole site's server side layout or only few server side
 * components.
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @Since 1.0.0
 */
public class WebBox {
	public static final String JWEBBOX_ID = "jwebbox";

	/** Optional, you can give a name to WebBox instance */
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
	 * A JSP or FTL page String, or a WebBox instance, or a WebBox class, 5th
	 * output, if not empty
	 */
	private Object page;

	/** Inside use hashmap store attributes */
	private Map<String, Object> attributeMap = new HashMap<>();

	/** Point to father WebBox instance if have */
	private WebBox fatherWebBox;

	private WebContext webContext;

	public WebBox() {
		// default constructor
	}

	/**
	 * Create a WebBox
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
	public void beforeShow() {// NOSONAR
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
	public WebBox show(WebContext webContext) {
		try {
			this.webContext = webContext;
			beforeShow();
			beforeExecute();
			execute();
			afterExecute();
			prepareOnly(webContext);
			afterPrepared();
			showText(webContext);
			showPageOrUrl(webContext, this.page, this);
			afterShow();
		} finally {
			this.webContext = null;
		}
		return this;
	}
	
	public WebBox show(PageContext pageContext) {
		return show(new WebContext(pageContext));
	}

	/** Direct out put the text into webContext.out */
	private WebBox showText(WebContext webContext) {
		if (text != null && text.length() > 0)
			webContext.print(text);
		return this;
	} 
 
	/** Prepare data, only but do not output text and do not show page */
	public void prepareOnly(WebContext webContext) {
		if (!isEmptyStr(prepareStaticMethod)) {
			int index = prepareStaticMethod.lastIndexOf('.');
			String className = prepareStaticMethod.substring(0, index);
			String methodName = prepareStaticMethod.substring(index + 1, prepareStaticMethod.length());
			if (isEmptyStr(className) || isEmptyStr(methodName))
				throw new WebBoxException("Error#001: Can not call method: " + prepareStaticMethod);
			try {
				Class<?> c = Class.forName(className);
				Method m = c.getMethod(methodName, WebContext.class, WebBox.class);
				m.invoke(c, webContext, this); // Call a static method
			} catch (Exception e) {
				throw new WebBoxException(e);
			}
		}
		if (prepareBean != null)
			executeBeanMethod(webContext);
		showPageOrUrl(webContext, this.prepareURL, this);
	}

	public void prepareOnly(PageContext pageContext) {
		prepareOnly(new WebContext(pageContext)); 
	}
	
	
	/** Execute Bean Method */
	private void executeBeanMethod(WebContext webContext) {
		try {
			Class<?> c = prepareBean.getClass();
			String methodName = isEmptyStr(prepareBeanMethod) ? "prepare" : prepareBeanMethod;
			Method m = c.getMethod(methodName, WebContext.class, WebBox.class);
			m.invoke(prepareBean, webContext, this); // Call a bean method
		} catch (Exception e) {
			throw new WebBoxException(e);
		}
	}

	/** Show page only, do not call prepareStaticMethod and URL */
	public WebBox showPageOnly(WebContext webContext) {
		return showPageOrUrl(webContext, this.page, this);
	}
	
	/** Show page only, do not call prepareStaticMethod and URL */
	public WebBox showPageOnly(PageContext pageContext) {
		return showPageOrUrl(new WebContext(pageContext), this.page, this);
	}

	/** Private method, use RequestDispatcher to show a URL or JSP page */
	private static WebBox showPageOrUrl(WebContext webContext, Object page, WebBox currentBox) {
		if (page == null)
			return currentBox;
		if (page instanceof WebBox) {
			((WebBox) page).show(webContext);
			return currentBox;
		}
		if (!(page instanceof String))
			throw new WebBoxException("" + page + " is not a String or WebBox.");
		String pageOrUrl = (String) page;
		if (isEmptyStr(pageOrUrl))
			return currentBox;
		WebBox fatherWebBox = (WebBox) webContext.getRequest().getAttribute(JWEBBOX_ID);
		if (fatherWebBox != null)
			currentBox.setFatherWebBox(fatherWebBox);
		webContext.getRequest().setAttribute(JWEBBOX_ID, currentBox);
		try {
			System.out.println("pageOrUrl="+pageOrUrl);
			System.out.println("webContext="+webContext);
			System.out.println("webContext="+webContext.getPageContext());
			webContext.getPageContext().getOut().flush();
			webContext.getPageContext().getRequest().getRequestDispatcher(pageOrUrl).include(webContext.getPageContext().getRequest(),
					webContext.getPageContext().getResponse());
			System.out.println("=========done======");
		} catch (Exception e) {
			throw new WebBoxException(e);
		} finally {
			webContext.getRequest().setAttribute(JWEBBOX_ID, fatherWebBox);
			currentBox.setFatherWebBox(null);
		}
		return currentBox;
	}

	/** Get current webContext's WebBox instance */
	public static WebBox getBox(WebContext webContext) {
		WebBox currentBox = (WebBox) webContext.getRequest().getAttribute(JWEBBOX_ID);
		if (currentBox == null)
			throw new WebBoxException("Error#003: Can not find WebBox instance in webContext");
		return currentBox;
	}

	/** Get an attribute from current page's WebBox instance */
	public static <T> T getAttribute(WebContext webContext, String attributeName) {
		return getBox(webContext).getAttribute(attributeName); 
	}

	/** Assume the value is String or WebBox instance, show it */
	public static void showAttribute(WebContext webContext, String attributeName) {
		Object obj = WebBox.getAttribute(webContext, attributeName);
		showTarget(webContext, obj);
	}

	/**
	 * Show an target object, target can be: WebBox instance or String or List of
	 * WebBox instance or String
	 */
	public static void showTarget(WebContext webContext, Object target) {
		if (target == null)
			return;
		if (target instanceof WebBox)
			((WebBox) target).show(webContext);
		else if (target instanceof ArrayList<?>) {
			for (Object item : (ArrayList<?>) target)
				showTarget(webContext, item);
		} else if (target instanceof Class) {
			WebBox bx = null;
			try {
				bx = (WebBox) ((Class<?>) target).newInstance();
			} catch (Exception e) {
				throw new WebBoxException("Can not create WebBox instance for target class '" + target + "'", e);
			}
			bx.show(webContext);
		} else if (target instanceof String) {
			String str = (String) target;
			if (str.startsWith("/")) {
				showPageOrUrl(webContext, str, getBox(webContext));
			} else {
				webContext.print(str);
			}
		} else
			throw new WebBoxException("Can not show unknow type object " + target + " on page");
	}

	// Getter & Setters

	/** Set attribute for current WebBox instance */
	public WebBox setAttribute(String key, Object value) {
		attributeMap.put(key, value);
		return this;
	}

	/** Get WebBox's attribute */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		Object obj = attributeMap.get(key);
		if (obj == null && webContext != null) {
			obj = webContext.getRequest().getAttribute(key);
			if (obj == null)
				obj = webContext.getRequest().getParameter(key);
			if (obj == null)
				obj = webContext.getPageContextAttribute(key);
			if (obj == null)
				obj = webContext.getSession().getAttribute(key);
		}
		return (T) obj;
	}

	/**
	 * Search and return an attribute object follow this order:
	 * webContext->request->parameter->session
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(String key) {
		if (webContext == null || key == null)
			return null;
		Object obj = webContext.getPageContextAttribute(key);
		if (obj == null)
			obj = webContext.getRequest().getAttribute(key);
		if (obj == null)
			obj = webContext.getRequest().getParameter(key);
		if (obj == null)
			obj = webContext.getSession().getAttribute(key);

		return (T) obj;
	}

	/** Set a webContext attribute */
	public void setPageContextAttribute(String key, Object value) {
		webContext.setPageContextAttribute(key, value);
	}

	/** Set a request attribute */
	public void setRequestAttribute(String key, Object value) {
		webContext.getRequest().setAttribute(key, value);
	}

	/** Set a session attribute */
	public void setSessionAttribute(String key, Object value) {
		webContext.getSession().setAttribute(key, value);//NOSONAR
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
			throw new WebBoxException("setPage method only accept String or WebBox instance type parameter");
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

	/** Get the attribute map of WebBox instance */
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	/** Set the attribute map for WebBox instance */
	public WebBox setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
		return this;
	}

	/** get the name of the WebBox instance */
	public String getName() {
		return name;
	}

	/** Set the name of the WebBox instance */
	public WebBox setName(String name) {
		this.name = name;
		return this;
	}

	/** Set the father page's WebBox instance */
	public WebBox getFatherWebBox() {
		return fatherWebBox;
	}

	/** Get the father page's WebBox instance */
	public void setFatherWebBox(WebBox fatherWebBox) {
		this.fatherWebBox = fatherWebBox;
	}

	/**
	 * Get current webContext if have
	 */
	public WebContext getWebContext() {
		return webContext;
	}

}
