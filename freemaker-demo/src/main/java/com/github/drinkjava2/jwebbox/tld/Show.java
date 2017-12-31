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
public class Show extends SimpleTagSupport {
	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void doTag() throws JspException, IOException {
//		System.out.println("====================in Show doTag");
//		System.out.println("attribute=" + attribute);
//		System.out.println("getJspContext()=" + getJspContext());
//		JspWriter out = getJspContext().getOut();
		WebBox.showAttribute((PageContext) getJspContext(), getAttribute());
	}
}