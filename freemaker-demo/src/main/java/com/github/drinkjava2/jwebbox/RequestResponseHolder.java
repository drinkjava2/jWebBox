/*
 * Copyright (C) 2016 Yong Zhu.
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * RequestResponseHolder used to keep request and response in threadLocal
 * variants, to use it at the beginning of a http request, need call
 * setHttpRequest or setHttpResponse first, usually set a servlet in web.xml do
 * this job
 * 
 * @author Yong Zhu(yong9981@gmail.com)
 * @since 2.1
 */
public class RequestResponseHolder implements Filter {
	private static ThreadLocal<HttpServletRequest> HttpRequestThreadLocalHolder = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> HttpResponseThreadLocalHolder = new ThreadLocal<HttpServletResponse>();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {		
		System.out.println("In filter");
		System.out.println("keep request="+request); 
		HttpRequestThreadLocalHolder.set((HttpServletRequest) request);
		HttpResponseThreadLocalHolder.set((HttpServletResponse) response); 
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() { 
	}

	// ======== getter & setter========

	public static void setHttpRequest(HttpServletRequest request) {
		HttpRequestThreadLocalHolder.set(request);
	}

	public static HttpServletRequest getHttpRequest() {
		System.out.println("get request="+HttpRequestThreadLocalHolder.get());
		return HttpRequestThreadLocalHolder.get();
	}

	public static void setHttpResponse(HttpServletResponse response) {
		HttpResponseThreadLocalHolder.set(response);
	}

	public static HttpServletResponse getHttpResponse() {

		return HttpResponseThreadLocalHolder.get();
	}

}
