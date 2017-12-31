package com.github.drinkjava2.jwebbox.tld;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.github.drinkjava2.jwebbox.WebBox;
import com.github.drinkjava2.jwebbox.WebBox.WebBoxException;

/**
 * This is to show a WebBox instance or a String URL as include mode in JSP page
 */
@SuppressWarnings({ "all" })
public class Include extends SimpleTagSupport {
	private Object target;

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void doTag() throws JspException, IOException { 
		WebBox.showObject((PageContext) getJspContext(), target);
	}
}