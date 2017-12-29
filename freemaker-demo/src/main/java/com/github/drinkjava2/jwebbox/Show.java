package com.github.drinkjava2.jwebbox;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

/**
 * This is a customized TLD
 */
@SuppressWarnings({ "all" })
public class Show extends SimpleTagSupport {
	public String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		WebBox.showAttribute((PageContext) getJspContext(), getAttribute());
	}
}