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
		doPageDispatch(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPageDispatch(request, response);
	}

	private void doPageDispatch(HttpServletRequest request, HttpServletResponse response) {
		String uri = StringUtils.substringBefore(request.getRequestURI(), ".");
		uri = StringUtils.substringAfterLast(uri, "/");
		if (uri == null || uri.length() == 0)
			uri = "homepage";
		try {
			WebBox box = (WebBox) Class.forName("com.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
			if (box != null)
				box.show(request, response);
			else
				new WebBox().setText("Not found page: " + uri);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException(e); // NOSONAR;
		}
	}
}