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
package com.github.drinkjava2.jwebbox.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.drinkjava2.jwebbox.WebBox;
import com.github.drinkjava2.jwebbox.WebBox.WebBoxException;
import com.github.drinkjava2.jwebbox.WebBoxStrUtils;

/**
 * WebBox is a small layout tool used in servlet environment
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @since v1.0.0
 */
public class HtmlRender {// NOSONAR
	private static final Map<String, HtmlItem[]> htmlCache = new ConcurrentHashMap<>();

	public static void renderAsHtml(HttpServletRequest request, HttpServletResponse response, String pageOrUrl)
			throws Exception {// NOSONAR
		HtmlItem[] items = getItemsFromPageOrUrl(pageOrUrl);
		if (items == null || items.length == 0)
			throw new WebBoxException("No pageOrUrl context found: " + pageOrUrl);
		for (HtmlItem item : items) {
			if (HtmlItemType.TEXT == item.type)
				response.getWriter().print((String) item.value);
			else if (HtmlItemType.URL == item.type) {
				request.getRequestDispatcher((String) item.value).include(request, response);
			} else if (HtmlItemType.BOX == item.type) {
				((WebBox) item.value).show(request, response);
			} else if (HtmlItemType.BOX_ATTRIBUTE == item.type) {
				WebBox box = WebBox.getBox(request);
				Object attribute = box.getAttribute((String) item.value);
				if (attribute != null)
					WebBox.showTarget(request, response, attribute);
			}
		}
	}

	private static HtmlItem[] getItemsFromPageOrUrl(String pageOrUrl) {
		if (WebBoxStrUtils.isEmpty(pageOrUrl))
			return new HtmlItem[0];
		HtmlItem[] stored = htmlCache.get(pageOrUrl);
		if (stored != null)
			return stored;
		String text = WebBoxStrUtils.readFile(WebBox.getWebappFolder() + pageOrUrl, "utf-8");

		HtmlItem[] result = splitToHtmlItems(text);
		htmlCache.put(pageOrUrl, result);
		return result;
	}

	/**
	 * split text to HtmlItems, for example, foo $$show(body) bar $$show(footer)
	 * will be split to:<br/>
	 * "foo" , HtmlItem(BOX_ATTRIBUTE="body"), "bar" ,
	 * HtmlItem(BOX_ATTRIBUTE="footer")
	 */
	private static HtmlItem[] splitToHtmlItems(String text) {
		if (WebBoxStrUtils.isEmpty(text))
			return new HtmlItem[0];

		List<HtmlItem> resultList = new ArrayList<>();
		String[] ss = text.split("\\$\\$show\\(");
		if (ss != null && ss.length > 0)
			resultList.add(new HtmlItem(ss[0], HtmlItemType.TEXT));
		if (ss != null)
			for (int i = 1; i < ss.length; i++) {
				String st = ss[i];
				int pos = st.indexOf(')');
				if (pos > 0) {
					String value = WebBoxStrUtils.substringBefore(st, ")");
					String leftOver = WebBoxStrUtils.substringAfter(st, ")");
					if (value.startsWith("/")) {
						// File to Text
						String fileContext = WebBoxStrUtils.readFile(WebBox.getWebappFolder() + value, "utf-8");
						HtmlItem[] childHtmlItems = splitToHtmlItems(fileContext);
						for (HtmlItem childItem : childHtmlItems)
							resultList.add(childItem);
					} else if (WebBoxStrUtils.startsWithIgnoreCase(value, "URL:")) {
						resultList.add(new HtmlItem(WebBoxStrUtils.substringAfter(value, ":"), HtmlItemType.URL));
					} else if (WebBoxStrUtils.startsWithIgnoreCase(value, "BOX:")) {
						String boxClaz = WebBoxStrUtils.substringAfter(value, ":");
						Class<?> clazz;
						try {
							clazz = Class.forName(boxClaz);
							WebBox box = (WebBox) clazz.newInstance();
							resultList.add(new HtmlItem(box, HtmlItemType.BOX));
						} catch (Exception e) {
							throw new WebBoxException(e);
						}
					} else
						resultList.add(new HtmlItem(value, HtmlItemType.BOX_ATTRIBUTE));
					resultList.add(new HtmlItem(leftOver, HtmlItemType.TEXT));
				} else
					resultList.add(new HtmlItem(st, HtmlItemType.TEXT));
			}
		return resultList.toArray(new HtmlItem[resultList.size()]);
	}

}
