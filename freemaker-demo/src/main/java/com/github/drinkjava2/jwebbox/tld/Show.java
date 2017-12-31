package com.github.drinkjava2.jwebbox.tld;

import java.io.IOException;

import javax.servlet.jsp.JspException;
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

	private Object target;

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void doTag() throws JspException, IOException {
		if (attribute != null && attribute.length() != 0)
			WebBox.showAttribute((PageContext) getJspContext(), getAttribute());
		if (target != null)
			WebBox.showTarget((PageContext) getJspContext(), target);
	}
}