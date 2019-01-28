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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * JspBox is a small layout tool used only for JSP pages, playing the same role
 * like Apache Tiles
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @version 2.1.2
 */
public class JspBox {
	public static final String JSPBOXID = "JSPBOX";

	/** Optional, you can give a name to JspBox instance */
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
	 * A JSP or FTL page String, or a JspBox instance, or a JspBox class, 5th
	 * output, if not empty
	 */
	private Object page;

	/** Inside use hashmap store attributes */
	private Map<String, Object> attributeMap = new HashMap<String, Object>();

	/** Point to father JspBox instance if have */
	private JspBox fatherJspBox;

	private PageContext pageContext;

	public JspBox() {
		// default constructor
	}

	/**
	 * Create a JspBox
	 * 
	 * @param page
	 *            The JSP or FTL or any URL, for example: "/template/abc.jsp"
	 */
	public JspBox(String page) {
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
	public void render() {// NOSONAR
	}

	/** For subclasses override this method to do something */
	public void afterShow() {// NOSONAR
	}

	/** Prepare data and out put text include page if have */
	public JspBox show(PageContext pageContext) {
		try {
			this.pageContext = pageContext;
			beforeExecute();
			execute();
			afterExecute();
			prepareOnly(pageContext);
			afterPrepared();
			showText(pageContext);
			render();
			showPageOrUrl(pageContext, this.page, this);
			afterShow();
		} finally {
			this.pageContext = null;
		}
		return this;
	}

	/** Direct out put the text into pageContext.out */
	private JspBox showText(PageContext pageContext) {
		if (text != null && text.length() > 0)
			try {
				pageContext.getOut().write(text);
			} catch (IOException e) {
				throw new JspBoxException(e);
			}
		return this;
	}

	/** Prepare data, only but do not output text and do not show page */
	public void prepareOnly(PageContext pageContext) {
		if (!isEmptyStr(prepareStaticMethod)) {
			int index = prepareStaticMethod.lastIndexOf('.');
			String className = prepareStaticMethod.substring(0, index);
			String methodName = prepareStaticMethod.substring(index + 1, prepareStaticMethod.length());
			if (isEmptyStr(className) || isEmptyStr(methodName))
				throw new JspBoxException("Error#001: Can not call method: " + prepareStaticMethod);
			try {
				Class<?> c = Class.forName(className);
				Method m = c.getMethod(methodName, PageContext.class, JspBox.class);
				m.invoke(c, pageContext, this); // Call a static method
			} catch (Exception e) {
				throw new JspBoxException(e);
			}
		}
		if (prepareBean != null)
			executeBeanMethod(pageContext);
		showPageOrUrl(pageContext, this.prepareURL, this);
	}

	/** Execute Bean Method */
	private void executeBeanMethod(PageContext pageContext) {
		try {
			Class<?> c = prepareBean.getClass();
			String methodName = isEmptyStr(prepareBeanMethod) ? "prepare" : prepareBeanMethod;
			Method m = c.getMethod(methodName, PageContext.class, JspBox.class);
			m.invoke(prepareBean, pageContext, this); // Call a bean method
		} catch (Exception e) {
			throw new JspBoxException(e);
		}
	}

	/** Show page only, do not call prepareStaticMethod and URL */
	public JspBox showPageOnly(PageContext pageContext) {
		return showPageOrUrl(pageContext, this.page, this);
	}

	/** Private method, use RequestDispatcher to show a URL or JSP page */
	private static JspBox showPageOrUrl(PageContext pageContext, Object page, JspBox currentBox) {
		if (page == null)
			return currentBox;
		if (page instanceof JspBox) {
			((JspBox) page).show(pageContext);
			return currentBox;
		}
		if (!(page instanceof String))
			throw new JspBoxException("" + page + " is not a String or JspBox.");
		String pageOrUrl = (String) page;
		if (isEmptyStr(pageOrUrl))
			return currentBox;
		JspBox fatherJspBox = (JspBox) pageContext.getRequest().getAttribute(JSPBOXID);
		if (fatherJspBox != null)
			currentBox.setFatherJspBox(fatherJspBox);
		pageContext.getRequest().setAttribute(JSPBOXID, currentBox);
		try {
			pageContext.getOut().flush();
			pageContext.getRequest().getRequestDispatcher(pageOrUrl).include(pageContext.getRequest(),
					pageContext.getResponse());
		} catch (Exception e) {
			throw new JspBoxException(e);
		} finally {
			pageContext.getRequest().setAttribute(JSPBOXID, fatherJspBox);
			currentBox.setFatherJspBox(null);
		}
		return currentBox;
	}

	/** Get current pageContext's JspBox instance */
	public static JspBox getBox(PageContext pageContext) {
		JspBox currentBox = (JspBox) pageContext.getRequest().getAttribute(JSPBOXID);
		if (currentBox == null)
			throw new JspBoxException("Error#003: Can not find JspBox instance in pageContext");
		return currentBox;
	}

	/** Get an attribute from current page's JspBox instance */
	public static <T> T getAttribute(PageContext pageContext, String attributeName) {
		T obj = getBox(pageContext).getAttribute(attributeName);
		return obj;
	}

	/** Assume the value is String or JspBox instance, show it */
	public static void showAttribute(PageContext pageContext, String attributeName) {
		Object obj = JspBox.getAttribute(pageContext, attributeName);
		showTarget(pageContext, obj);
	}

	/**
	 * Show an target object, target can be: JspBox instance or String or List of
	 * JspBox instance or String
	 */
	public static void showTarget(PageContext pageContext, Object target) {
		if (target == null)
			return;
		if (target instanceof JspBox)
			((JspBox) target).show(pageContext);
		else if (target instanceof ArrayList<?>) {
			for (Object item : (ArrayList<?>) target)
				showTarget(pageContext, item);
		} else if (target instanceof Class) {
			JspBox bx = null;
			try {
				bx = (JspBox) ((Class<?>) target).newInstance();
			} catch (Exception e) {
				throw new JspBoxException("Can not create JspBox instance for target class '" + target + "'", e);
			}
			bx.show(pageContext);
		} else if (target instanceof String) {
			String str = (String) target;
			if (str.startsWith("/")) {
				showPageOrUrl(pageContext, str, getBox(pageContext));
			} else {
				try {
					pageContext.getOut().write(str);
				} catch (IOException e) {
					throw new JspBoxException(e);
				}
			}
		} else
			throw new JspBoxException("Can not show unknow type object " + target + " on page");
	}

	// Getter & Setters

	/** Set attribute for current JspBox instance */
	public JspBox setAttribute(String key, Object value) {
		attributeMap.put(key, value);
		return this;
	}

	/** Get JspBox's attribute */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		Object obj = attributeMap.get(key);
		if (obj == null && pageContext != null) {
			obj = pageContext.getRequest().getAttribute(key);
			if (obj == null)
				obj = pageContext.getRequest().getParameter(key);
			if (obj == null)
				obj = pageContext.getAttribute(key);
			if (obj == null)
				obj = pageContext.getSession().getAttribute(key);
		}
		return (T) obj;
	}

	/**
	 * Search and return an attribute object follow this order:
	 * pageContext->request->parameter->session
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(String key) {
		if (pageContext == null || key == null)
			return null;
		Object obj = pageContext.getAttribute(key);
		if (obj == null)
			obj = pageContext.getRequest().getAttribute(key);
		if (obj == null)
			obj = pageContext.getRequest().getParameter(key);
		if (obj == null)
			obj = pageContext.getSession().getAttribute(key);

		return (T) obj;
	}

	/** Set a pageContext attribute */
	public void setPageContextAttribute(String key, Object value) {
		pageContext.setAttribute(key, value);
	}

	/** Set a request attribute */
	public void setRequestAttribute(String key, Object value) {
		pageContext.getRequest().setAttribute(key, value);
	}

	/** Set a session attribute */
	public void setSessionAttribute(String key, Object value) {
		pageContext.getSession().setAttribute(key, value);
	}

	/** Get the prepare URL */
	public String getPrepareURL() {
		return prepareURL;
	}

	/**
	 * Set prepare URL, this URL be called after prepare methods but before show
	 * page
	 */
	public JspBox setPrepareURL(String prepareURL) {
		this.prepareURL = prepareURL;
		return this;
	}

	/** Get the page */
	public Object getPage() {
		return page;
	}

	/** Set a JSP page or URL */
	public JspBox setPage(Object page) {
		if (!(page == null || page instanceof String || page instanceof JspBox))
			throw new JspBoxException("setPage method only accept String or JspBox instance type parameter");
		this.page = page;
		return this;
	}

	/** Get the Text */
	public String getText() {
		return text;
	}

	/** Set the text String */
	public JspBox setText(String text) {
		this.text = text;
		return this;
	}

	/** Set a prepare static method */
	public JspBox setPrepareStaticMethod(String prepareStaticMethod) {
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
	public JspBox setPrepareBean(Object prepareBean) {
		this.prepareBean = prepareBean;
		return this;
	}

	/** Get the prepare bean method name */
	public String getPrepareBeanMethod() {
		return prepareBeanMethod;
	}

	/** Set the bean prepare method name */
	public JspBox setPrepareBeanMethod(String prepareBeanMethod) {
		this.prepareBeanMethod = prepareBeanMethod;
		return this;
	}

	/** Get the attribute map of JspBox instance */
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}

	/** Set the attribute map for JspBox instance */
	public JspBox setAttributeMap(Map<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
		return this;
	}

	/** get the name of the JspBox instance */
	public String getName() {
		return name;
	}

	/** Set the name of the JspBox instance */
	public JspBox setName(String name) {
		this.name = name;
		return this;
	}

	/** Set the father page's JspBox instance */
	public JspBox getFatherJspBox() {
		return fatherJspBox;
	}

	/** Get the father page's JspBox instance */
	public void setFatherJspBox(JspBox fatherJspBox) {
		this.fatherJspBox = fatherJspBox;
	}

	/**
	 * Get current pageContext if have
	 */
	public PageContext getPageContext() {
		return pageContext;
	}

	/**
	 * Set pageContext to it
	 */
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	/** A runtime exception caused by JspBox */
	public static class JspBoxException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JspBoxException(String msg) {
			super(msg);
		}

		public JspBoxException(Throwable e) {
			super(e);
		}

		public JspBoxException(String msg, Throwable e) {
			super(msg, e);
		}
	}

	/** This is a custom TagLib for JSP and also can be used in FreeMaker */
	public static class Show extends SimpleTagSupport {
		private String attribute;

		public String getAttribute() {
			return attribute;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

		private Object target;

		public Object getTarget() {
			return target;
		}

		public void setTarget(Object target) {
			this.target = target;
		}

		public void doTag() throws JspException, IOException {
			if (attribute != null && attribute.length() != 0)
				JspBox.showAttribute((PageContext) getJspContext(), getAttribute());
			if (target != null)
				JspBox.showTarget((PageContext) getJspContext(), target);
		}
	}
}
