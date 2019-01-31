/* Copyright 2018-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.demo;

import org.junit.Test;

import com.demo.jspmock.MockPageContext;
import com.demo.jspmock.MockRequest;
import com.demo.jspmock.MockResponse;
import com.jwebboxdemo.Dispatcher;

/**
 * Unit test of Dao
 * 
 * @author Yong Zhu
 * @since 1.0.0
 */
public class MockTest {

	@Test
	public void testHomepage() {
		MockPageContext pageContext = new MockPageContext();
		MockRequest request = (MockRequest) pageContext.getRequest();
		MockResponse response = new MockResponse();
		request.setRequestURI("/homepage.page");
		Dispatcher.doDispatch(request, response);
		System.out.println(MockResponse.baos.toString());
	}

}