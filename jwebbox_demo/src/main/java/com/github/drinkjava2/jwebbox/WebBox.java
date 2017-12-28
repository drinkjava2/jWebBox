/*
 * Copyright (C) 2016 Yong Zhu.
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
import java.util.Random;

import javax.servlet.jsp.PageContext;

/**
 * JWebBox is a small layout tool used in java server pages(JSP) projects,
 * playing the same role like Apache Tiles and SiteMesh, no XML file, no Tags,
 * simple(only 1 java file), can be used to support whole site's layout or only
 * few page components.
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @version 2.1
 */
public class WebBox {
	// A static method to prepare data, first be called if have
	private String prepareStaticMethod;

	// A Bean have a "prepare" methods to prepare data, 2nd called
	private Object prepareBean;

	// If prepareBeanMethod is set, use it instead of use bean's "prepare" method
	private String prepareBeanMethod;

	private String prepareURL;// A URL, 3rd called, if have

	private String text; // A text , 4th output, if not empty

	private String page;// A JSP page, 5th output, if not empty

	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	private WebBox parent; // parentBox

	public WebBox() {
		// default constructor
	}

	/**
	 * Create a WebBox
	 * 
	 * @param page The JSP file location, an example: "/template/abc.jsp"
	 */
	public WebBox(String page) {
		this.setPage(page);
	}

	/** Check if String null or empty */
	public static boolean isEmptyStr(String str) {
		return (str == null || str.length() == 0);
	}

	/** Prepare data and show page */
	public void show(PageContext pageContext) {
		prepareOnly(pageContext);
		if (text != null && text.length() > 0)
			try {
				pageContext.getOut().write(text);
			} catch (IOException e) {
				throw new WebBoxException(e);
			}
		showPageOrUrl(pageContext, this.page, this);
	}

	/** Prepare data only, call prepareStaticMethod and URL, do not show page */
	public void prepareOnly(PageContext pageContext) {
		if (!isEmptyStr(prepareStaticMethod)) {
			int index = prepareStaticMethod.lastIndexOf('.');
			String className = prepareStaticMethod.substring(0, index);
			String methodName = prepareStaticMethod.substring(index + 1, prepareStaticMethod.length());
			if (isEmptyStr(className) || isEmptyStr(methodName))
				throw new WebBoxException("Error#001: Can not call method: " + prepareStaticMethod);
			executePrepareStaticMethod(pageContext, className, methodName);
		}
		if (prepareBean != null)
			executeBeanMethod(pageContext);
		showPageOrUrl(pageContext, this.prepareURL, this);
	}

	private void executePrepareStaticMethod(PageContext pageContext, String className, String methodName) {
		try {
			Class<?> c = Class.forName(className);
			Method m = c.getMethod(methodName, PageContext.class, WebBox.class);
			m.invoke(c, pageContext, this); // Call a static method
		} catch (Exception e) {
			throw new WebBoxException(e);
		}
	}

	private void executeBeanMethod(PageContext pageContext) {
		try {
			Class<?> c = prepareBean.getClass();
			String methodName = isEmptyStr(prepareBeanMethod) ? "prepare" : prepareBeanMethod;
			Method m = c.getMethod(methodName, PageContext.class, WebBox.class);
			m.invoke(prepareBean, pageContext, this); // Call a bean method
		} catch (Exception e) {
			throw new WebBoxException(e);
		}
	}

	/** Show page only, do not call prepareStaticMethod and URL */
	public void showPageOnly(PageContext pageContext) {
		showPageOrUrl(pageContext, this.page, this);
	}

	/** Private method, use RequestDispatcher to show a URL or JSP page */
	private static void showPageOrUrl(PageContext pageContext, String pageOrUrl, WebBox caller) {
		if (isEmptyStr(pageOrUrl))
			return;
		Random rand = new Random();
		String boxCallerID = Long.toString(rand.nextLong()) + "_" + rand.nextLong() + "_" + rand.nextLong();
		pageContext.getRequest().setAttribute(boxCallerID, caller);// put caller
		try {
			pageContext.getOut().flush();
		} catch (IOException e1) {
			throw new WebBoxException(e1);
		}
		try {
			pageContext.getRequest()
					.getRequestDispatcher(
							pageOrUrl + ((pageOrUrl).indexOf("?") >= 0 ? "&" : "?") + "boxCallerID=" + boxCallerID)
					.include(pageContext.getRequest(), pageContext.getResponse());

		} catch (Exception e) {
			throw new WebBoxException(e);
		}
	}

	/** Get current pageContext's WebBox instance */
	public static WebBox getBox(PageContext pageContext) {
		String boxCallerID = pageContext.getRequest().getParameter("boxCallerID");
		if (isEmptyStr(boxCallerID))
			throw new WebBoxException("Error#002: Can not find boxCallerID in parameters");
		WebBox box = (WebBox) pageContext.getRequest().getAttribute(boxCallerID);
		if (box == null)
			throw new WebBoxException("Error#003: Can not find caller box in pageContext");
		return box;
	}

	/** Get an attribute from current page's WebBox instance */
	public static <T> T getAttribute(PageContext pageContext, String attributeName) {
		return getBox(pageContext).getAttribute(attributeName);
	}

	/** Assume the value is String or WebBox instance, show it */
	public static void showAttribute(PageContext pageContext, String attributeName) {
		Object obj = WebBox.getAttribute(pageContext, attributeName);
		showObject(pageContext, obj);
	}

	/**
	 * Show an unknown object, object can be one of below: WebBox instance,
	 * String,List of WebBox instance, List of String
	 */
	public static void showObject(PageContext pageContext, Object obj) {
		if (obj == null)
			return;
		if (obj instanceof WebBox)
			((WebBox) obj).show(pageContext);
		else if (obj instanceof ArrayList<?>) {
			for (Object item : (ArrayList<?>) obj) {
				showObject(pageContext, item);
			}
		} else if (obj instanceof String) {
			String str = "" + obj;
			if (str.startsWith("/")) {
				showPageOrUrl(pageContext, str, getBox(pageContext));
			} else {
				try {
					pageContext.getOut().write(str);
				} catch (IOException e) {
					throw new WebBoxException(e);
				}
			}
		}
	}

	/** Set attribute for current WebBox instance */
	public WebBox setAttribute(String key, Object value) {
		attributeMap.put(key, value);
		return this;
	}

	/** Get attribute from current WebBox instance */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributeMap.get(key);
	}

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

	public String getPage() {
		return page;
	}

	/** Set a JSP page or URL */
	public WebBox setPage(String page) {
		this.page = page;
		return this;
	}

	public String getText() {
		return text;
	}

	/** Set the text String */
	public WebBox setText(String text) {
		this.text = text;
		return this;
	}

	/** Get the parent WebBox instance */
	public WebBox getParent() {
		return parent;
	}

	/** set the parent WebBox */
	public WebBox setParent(WebBox parent) {
		this.parent = parent;
		return this;
	}

	/** Set a prepare static method */
	public WebBox setPrepareStaticMethod(String prepareStaticMethod) {
		this.prepareStaticMethod = prepareStaticMethod;
		return this;
	}

	public String getPrepareStaticMethod() {
		return prepareStaticMethod;
	}

	public Object getPrepareBean() {
		return prepareBean;
	}

	/** Set a prepare bean which has a prepare method */
	public WebBox setPrepareBean(Object prepareBean) {
		this.prepareBean = prepareBean;
		return this;
	}

	public String getPrepareBeanMethod() {
		return prepareBeanMethod;
	}

	/** Set the bean prepare method name */
	public WebBox setPrepareBeanMethod(String prepareBeanMethod) {
		this.prepareBeanMethod = prepareBeanMethod;
		return this;
	}

	@SuppressWarnings("serial")
	public static class WebBoxException extends RuntimeException {
		public WebBoxException(String msg) {
			super(msg);
		}

		public WebBoxException(Throwable e) {
			super(e); 
		}
	}
}
