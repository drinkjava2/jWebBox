package com.jwebbox;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.jsp.PageContext;

/**
 * JWebBox2.0.0 is a small layout tool used in java server pages(JSP) projects, playing the same role like Tiles and SiteMesh, no XML file, no Tags, simple(only 1 java class) and easy to use, it can
 * be used to build whole web site or only few page components. JWebBox2.0.0 follows BSD license.
 * 
 * @author Yong
 * @since 2016-1-30
 */
@SuppressWarnings("unchecked")
public class Box {
	private String prepareStaticMethod;// 1st called, if have

	private String prepareBeanMethod;// 2nd called, if have

	private String prepareURL;// 3rd called, if have

	private String page;// 4th display, if have

	private HashMap<String, Object> attributeMap = new HashMap<String, Object>();

	private PageContext _pageContext = null; 

	private static boolean isEmptyStr(String str) {
		return (str == null || "".equals(str));
	}

	/**
	 * Create a Box 
	 */
	public Box(){
	}
	
	/**
	 * Create a Box 
	 * @param page
	 */
	public Box(String page){
		this.setPage(page);
	}
	
	/**
	 * Show box in JSP page
	 * 
	 * @param pageContext
	 */
	public void show(PageContext pageContext) {
		prepareOnly(pageContext);
		showPageOrUrl(pageContext, this.page, this);
	}

	/**
	 * Only call preprare methods, do not show page
	 * 
	 * @param pageContext
	 */
	public void prepareOnly(PageContext pageContext) {
		if (!isEmptyStr(prepareStaticMethod))
			executeMethod(pageContext, prepareStaticMethod, true);
		if (!isEmptyStr(prepareBeanMethod))
			executeMethod(pageContext, prepareBeanMethod, false);
		showPageOrUrl(pageContext, this.prepareURL, this);
	}

	/**
	 * Only show page, do not call prepare methods
	 * 
	 * @param pageContext
	 */
	public void showPageOnly(PageContext pageContext) {
		showPageOrUrl(pageContext, this.page, this);
	}

	/**
	 * Private method, call prepare method
	 * 
	 * @param pageContext
	 * @param methodFullName
	 * @param isStaticMethod
	 */
	private void executeMethod(PageContext pageContext, String methodFullName, boolean isStaticMethod) {
		int index = methodFullName.lastIndexOf(".");
		String className = methodFullName.substring(0, index);
		String methodName = methodFullName.substring(index + 1, methodFullName.length());
		if (isEmptyStr(className) || isEmptyStr(methodName)) {
			System.out.println("Error#001: Can not call method: " + methodFullName);
			return;
		}
		try {
			Class c = Class.forName(className);
			if (isStaticMethod) {
				Method m = c.getMethod(methodName, new Class[] { PageContext.class, Box.class });
				m.invoke(c, new Object[] { pageContext, this }); // Call a static method
			} else {
				Method m = c.getMethod("getInstance", new Class[] { PageContext.class });// Call getInstance from a Class
				Object o = m.invoke(c, new Object[] { pageContext });
				m = c.getMethod(methodName, new Class[] { PageContext.class, Box.class });
				m.invoke(o, new Object[] { pageContext, this });// run this instance
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Private method, include page or URL
	 * 
	 * @param pageContext
	 * @param pageOrUrl
	 * @param caller
	 */
	private static void showPageOrUrl(PageContext pageContext, String pageOrUrl, Box caller) {
		if (isEmptyStr(pageOrUrl))
			return;
		Random rand = new Random();
		String boxCallerID = "" + rand.nextInt(2147483647) + rand.nextInt(2147483647) + rand.nextInt(2147483647) + rand.nextInt(2147483647);
		pageContext.getRequest().setAttribute(boxCallerID, caller);// put caller
		try {
			org.apache.jasper.runtime.JspRuntimeLibrary.include(pageContext.getRequest(), pageContext.getResponse(), pageOrUrl + ((pageOrUrl).indexOf('?') > 0 ? '&' : '?') + "boxCallerID="
					+ boxCallerID, pageContext.getOut(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pageContext.getRequest().removeAttribute(boxCallerID);
	}

	/**
	 * Get a Box instance from pageContext
	 * 
	 * @param pageContext
	 * @return
	 */
	public static Box getBox(PageContext pageContext) {
		String boxCallerID = pageContext.getRequest().getParameter("boxCallerID");
		if (isEmptyStr(boxCallerID)) {
			System.out.println("Error#002: Can not find boxCallerID in parameters!");
			return null;
		}
		Box box = (Box) pageContext.getRequest().getAttribute(boxCallerID);
		if (box == null) {
			System.out.println("Error#003: Can not find caller box in pageContext!");
			return null;
		}
		((Box) box)._pageContext = pageContext;
		return box;
	}

	/**
	 * Get a box attribute by attribute name, box is hidden in pageContext
	 * 
	 * @param pageContext
	 * @param attributeName
	 * @return
	 */
	public static Object getBoxAttribute(PageContext pageContext, String attributeName) {
		Box box = getBox(pageContext);
		if (box == null) {
			System.out.println("Error#004: Can not find box in pageContext!");
			return null;
		}
		Object o = box.getAttribute(attributeName);
		if (o == null)
			System.out.println("Error#005: Can not find attribute " + attributeName + " in box!");
		return o;
	}

	/**
	 * Show attribute on JSP page
	 * 
	 * @param attributeName
	 */
	public void showAttribute(String attributeName) {
		Object o = Box.getBoxAttribute(this._pageContext, attributeName);
		displayBoxObjectOnJspPage(this._pageContext, o, false);
	}

	/**
	 * Show attribute on JSP page, same as Box.getBox(pageContext).showAttribute(attributeName)
	 * 
	 * @param pageContext
	 * @param attributeName
	 */
	public static void showAttribute(PageContext pageContext, String attributeName) {
		Object o = Box.getBoxAttribute(pageContext, attributeName);
		displayBoxObjectOnJspPage(pageContext, o, false);
	}

	/**
	 * Show showAttributeList on JSP page, if cascadeShow is true, cascade show all child attributes
	 * 
	 * @param attributeName
	 * @param cascadeShow
	 */
	public void showAttributeList(String attributeName, boolean cascadeShow) {
		showAttributeList(this._pageContext, attributeName, cascadeShow);
	}

	/**
	 * Show showAttributeList on JSP page, same as Box.getBox(pageContext).showAttributeList
	 * 
	 * @param pageContext
	 * @param attributeName
	 * @param cascadeShow
	 */
	public static void showAttributeList(PageContext pageContext, String attributeName, boolean cascadeShow) {
		Object list = Box.getBoxAttribute(pageContext, attributeName);
		if (list instanceof ArrayList<?>) {
			for (Object item : (ArrayList<?>) list) {
				displayBoxObjectOnJspPage(pageContext, item, cascadeShow);
			}
		}
	}

	/**
	 * private method, show an object or string on JSP page
	 * 
	 * @param pageContext
	 * @param o
	 * @param cascadeShow
	 */
	private static void displayBoxObjectOnJspPage(PageContext pageContext, Object o, boolean cascadeShow) {
		if (o == null)
			return;
		if (o instanceof Box)
			((Box) o).show(pageContext);
		else if (o instanceof ArrayList<?> && cascadeShow) {
			ArrayList<?> l = (ArrayList<?>) o;
			for (Object item : l)
				displayBoxObjectOnJspPage(pageContext, item, cascadeShow);
		} else if (o instanceof String) {
			String str = "" + o;
			if (str.startsWith("/")) {
				showPageOrUrl(pageContext, str, getBox(pageContext));
			} else {
				try {
					pageContext.getOut().write(str);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Set Box attribute
	 * 
	 * @param key
	 * @param value
	 * @return Box
	 */
	public Box setAttribute(String key, Object value) {
		attributeMap.put(key, value);
		return this;
	}

	/**
	 * Get box attribute
	 * 
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key) {
		return attributeMap.get(key);
	}

	public String getPrepareStaticMethod() {
		return prepareStaticMethod;
	}

	/**
	 * Set Prepare Static Method
	 * 
	 * @param prepareStaticMethod
	 * @return Box
	 */
	public Box setPrepareStaticMethod(String prepareStaticMethod) {
		this.prepareStaticMethod = prepareStaticMethod;
		return this;
	}

	public String getPrepareBeanMethod() {
		return prepareBeanMethod;
	}

	/**
	 * Set prepare method methodname(PageContext pageContext, Box callerBox) in a class, which should have a static method getInstance(PageContext pageContext)
	 * 
	 * @param prepareBeanMethod
	 * @return
	 */
	public Box setPrepareBeanMethod(String prepareBeanMethod) {
		this.prepareBeanMethod = prepareBeanMethod;
		return this;
	}

	public String getPrepareURL() {
		return prepareURL;
	}

	/**
	 * Set prepare URL, this URL be called after prepare methods but before template page
	 * 
	 * @param prepareURL
	 * @return
	 */
	public Box setPrepareURL(String prepareURL) {
		this.prepareURL = prepareURL;
		return this;
	}

	public String getPage() {
		return page;
	}

	/**
	 * Set template or view jsp page
	 * 
	 * @param page
	 * @return
	 */
	public Box setPage(String page) {
		this.page = page;
		return this;
	}

	public HashMap<String, Object> getAttributeMap() {
		return attributeMap;
	}

	public Box setAttributeMap(HashMap<String, Object> attributeMap) {
		this.attributeMap = attributeMap;
		return this;
	}

}
