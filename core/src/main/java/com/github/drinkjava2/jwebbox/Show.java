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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Show is a JSP tag, can only used in JSP environment
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @Since 1.0.0
 */
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

	@Override
	public void doTag() throws JspException, IOException {
		if (attribute != null && attribute.length() != 0)
			WebBox.showAttribute(new WebContext((PageContext) getJspContext()), getAttribute());
		if (target != null)
			WebBox.showTarget(new WebContext((PageContext) getJspContext()), target);
	}
}
