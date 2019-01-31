package com.jwebboxdemo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.github.drinkjava2.jwebbox.WebBox;

public class Dispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doDispatch(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doDispatch(request, response);
	}

	public static void doDispatch(HttpServletRequest request, HttpServletResponse response) {
		WebBox box = findWebBox(request, response);
		box.show(request, response);
	}

	public static WebBox findWebBox(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		String uri = StringUtils.substringBefore(request.getRequestURI(), ".");
		uri = StringUtils.substringAfterLast(uri, "/");
		if (uri == null || uri.length() == 0)
			uri = "demo1";
		WebBox box;
		try {
			box = (WebBox) Class.forName("com.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
			if (box == null)
				box = new WebBox().setText("Not found page: " + uri);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			box = new WebBox().setText("Exception found for find: " + uri);
		}
		return box;
	}
}