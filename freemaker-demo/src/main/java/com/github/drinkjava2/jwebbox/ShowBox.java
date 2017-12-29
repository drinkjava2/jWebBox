package com.github.drinkjava2.jwebbox;

import java.io.IOException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

/**
 * This class is only used to show error and debug information.
 * 
 */
@SuppressWarnings({ "all" })
public class ShowBox extends SimpleTagSupport {
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		out.println("Hello Custom Tag!");
	}
}