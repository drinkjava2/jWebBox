package com.jwebboxdemo;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.drinkjava2.jwebbox.JspBox;
import com.github.drinkjava2.jwebbox.WebBox;

@SuppressWarnings("all")
public class DemoBoxConfig {

	public static class baseBox extends WebBox {
	}

	public static class demo1 extends baseBox {
		{
			setPage("/WEB-INF/pages/layout.htm");
			setAttribute("body", new body());
			setAttribute("footer", new footer());
		}
	}

	public static class demo2 extends demo1 {
		{
			setAttribute("body", new leftRight());
		}
	}

	public static class demo3 extends demo1 {
		{
			setAttribute("body", new topDown());
		}
	}

	public static class leftRight extends baseBox {
		{
			setPage("/WEB-INF/pages/left_right_layout.htm");
			setAttribute("page1", "/WEB-INF/pages/page1.htm");
			setAttribute("page2", "/WEB-INF/pages/page2.htm");
		}
	}

	public static class topDown extends leftRight {
		{
			setPage("/WEB-INF/pages/top_down_layout.htm");
		}
	}

	public static class demo4 extends demo1 {
		{
			ArrayList<Object> child = new ArrayList<Object>();
			for (int i = 1; i <= 3; i++)
				child.add(new WebBox("/WEB-INF/pages/page" + i + ".htm").setText("&nbsp;&nbsp;&nbsp;&nbsp;Child: "));
			ArrayList<Object> mainList = new ArrayList<Object>();
			for (int i = 1; i <= 3; i++) {
				mainList.add("/WEB-INF/pages/page" + i + ".htm");
				if (i == 2)
					mainList.add(child);
			}
			this.setAttribute("body", mainList);
		}
	}

	public static class body extends baseBox {
		{
			setPage("/WEB-INF/pages/body.htm");
		}
	}

	public static class footer extends baseBox {
		{
			setPage("/WEB-INF/pages/footer.htm");
		}
	}

	public static class demo5 extends demo1 {
		{
			setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".changeMenu");
			setAttribute("body", new WebBox().setText("<div style=\"width:900px\"> This is body text </div>")
					.setPrepareURL("/WEB-INF/pages/prepare.htm").setPrepareBean(new Printer()));
			setAttribute("footer", new WebBox("/WEB-INF/pages/footer.htm").setPrepareBean(new Printer())
					.setPrepareBeanMethod("print"));
		}
	}

	public static void changeMenu(HttpServletRequest request, HttpServletResponse response, WebBox callerBox)
			throws IOException {
		callerBox.setAttribute("msg", "This is set by \"changeMenu\" static method");
	}

	public static class Printer {
		public void prepare(HttpServletRequest request, HttpServletResponse response, WebBox callerBox)
				throws IOException {
			response.getWriter().write("This is printed by Printer's default \"prepare\" method <br/>");
		}

		public void print(HttpServletRequest request, HttpServletResponse response, WebBox callerBox)
				throws IOException {
			 response.getWriter().print("This is printed by Printer's \"print\" method <br/>");
		}
	}
}
