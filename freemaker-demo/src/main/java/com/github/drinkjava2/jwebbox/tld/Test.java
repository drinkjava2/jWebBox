package com.github.drinkjava2.jwebbox.tld;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.github.drinkjava2.jwebbox.WebBox;

/**
 * This is to show a WebBox's attribute in a JSP page, webBox instance is hide
 * inside of pageContext
 */
@SuppressWarnings({ "all" })
public class Test extends SimpleTagSupport { 

	public void doTag() throws JspException, IOException {
		System.out.println("====================in test doTag"); 
		System.out.println("getJspContext()=" + getJspContext());
		JspWriter out = getJspContext().getOut();
		out.write("test out put");
	}
}