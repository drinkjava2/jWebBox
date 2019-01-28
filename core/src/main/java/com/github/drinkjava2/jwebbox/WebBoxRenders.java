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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebBox is a small layout tool used in servlet environment
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @since v1.0.0
 */
public class WebBoxRenders {
	static String webinfPath = WebBoxRenders.class.getClassLoader().getResource("/").getPath();
	static {
		webinfPath = WebBoxStrUtils.substringBefore(webinfPath, "/WEB-INF");
	}

	public static void renderAsInclude(HttpServletRequest request, HttpServletResponse response, String pageOrUrl)
			throws Exception {
		request.getRequestDispatcher(pageOrUrl).include(request, response);
	}

	private static final Map<String, String[]> htmlCache = new ConcurrentHashMap<String, String[]>();

	public static void renderAsHtml(HttpServletRequest request, HttpServletResponse response, String pageOrUrl)
			throws Exception {
		if (WebBoxStrUtils.isEmpty(pageOrUrl))
			return;
		request.getRequestDispatcher(pageOrUrl).include(request, response);
//		String[] stored = htmlCache.get(pageOrUrl);
//		if (stored != null) {
//			for (String str : stored)
//				response.getWriter().print(str);
//		} else {
//			String readed = WebBoxStrUtils.readFile(webinfPath + pageOrUrl, "utf-8");
//			if (readed == null)
//				readed = "";
//			 //TODO
//		}
	}

}
