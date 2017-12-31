package com.github.drinkjava2.jwebboxdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

import com.github.drinkjava2.jwebbox.WebBox;

@SuppressWarnings("all")
public class DemoBoxConfig {

	// demo1 - The home page
	public static class demo1 extends WebBox {
		{
			this.setPage("/WEB-INF/pages/homepage.jsp");
			this.setAttribute("menu",
					new WebBox("/WEB-INF/pages/menu.jsp").setAttribute("msg", "Demo1 - A basic layout"));
			this.setAttribute("body", new LeftRightLayout());
			this.setAttribute("footer", new WebBox("/WEB-INF/pages/footer.jsp"));
		}
	}

	// A left-right layout
	public static class LeftRightLayout extends WebBox {
		{
			this.setPage("/WEB-INF/pages/left_right_layout.jsp");
			ArrayList<WebBox> boxlist = new ArrayList<WebBox>();
			boxlist.add(new WebBox().setPage("/WEB-INF/pages/page1.jsp"));
			boxlist.add(new WebBox().setPage("/WEB-INF/pages/page2.jsp"));
			this.setAttribute("boxlist", boxlist);
		}
	}

	// demo2 - Change body layout
	public static class demo2 extends demo1 {
		{
			((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo2 - Change body layout");
			this.setAttribute("body", new TopDownLayout());
		}
	}

	// A top-down layout
	public static class TopDownLayout extends LeftRightLayout {
		{
			this.setPage("/WEB-INF/pages/top_down_layout.jsp");
		}
	}

	// demo3 - Prepare methods
	public static class demo3 extends demo1 {
		{
			setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".changeMenu");
			setAttribute("body", new WebBox().setText("<div style=\"width:900px\"> This is body text </div>")
					.setPrepareURL("/WEB-INF/pages/prepare.jsp").setPrepareBean(new Printer()));
			setAttribute("footer", new WebBox("/WEB-INF/pages/footer.jsp").setPrepareBean(new Printer())
					.setPrepareBeanMethod("print"));
		}
	}

	public static void changeMenu(PageContext pageContext, WebBox callerBox) throws IOException {
		((WebBox) callerBox.getAttribute("menu")).setAttribute("msg",
				"Demo3 - Prepare methods <br/>This is modified by \"changeMenu\" static method");
	}

	public static class Printer {
		public void prepare(PageContext pageContext, WebBox callerBox) throws IOException {
			pageContext.getOut().write("This is printed by Printer's default \"prepare\" method <br/>");
		}

		public void print(PageContext pageContext, WebBox callerBox) throws IOException {
			pageContext.getOut().write("This is printed by Printer's \"print\" method <br/>");
			pageContext.getOut().write((String) pageContext.getRequest().getAttribute("urlPrepare"));
		}
	}

	// demo4 - List
	public static class demo4 extends demo1 {
		{
			((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo4 - List");
			ArrayList<Object> boxlist1 = new ArrayList<Object>();
			for (int i = 1; i <= 3; i++)
				boxlist1.add(new WebBox().setPage("/WEB-INF/pages/page" + i + ".jsp"));
			this.setAttribute("body", boxlist1);
		}
	}

	// demo5 - Freemaker demo
	public static class demo5 extends WebBox {
		{
			this.setPage("/WEB-INF/pages/homepage.ftl");
			this.setAttribute("menu",
					new WebBox("/WEB-INF/pages/menu.jsp").setAttribute("msg", "Demo5 - Freemaker demo"));
			this.setAttribute("body", new FreemakerLayout());
			this.setAttribute("footer", new WebBox("/WEB-INF/pages/footer.ftl"));
		}
	}

	// A left-right Freemaker layout
	public static class FreemakerLayout extends WebBox {
		{
			this.setPage("/WEB-INF/pages/freemaker_layout.ftl");
			ArrayList<Object> boxlist = new ArrayList<Object>();
			boxlist.add("/WEB-INF/pages/page1.ftl");
			boxlist.add(new WebBox("/WEB-INF/pages/page2.ftl"));
			boxlist.add("/WEB-INF/pages/page3.jsp");
			this.setAttribute("boxlist", boxlist);
		}
	}

	// demo6 - Table & Pagination
	public static final List<String> tableDummyData = new ArrayList<String>();
	public static final List<String> commentDummyData = new ArrayList<String>();
	static {
		for (int i = 1; i <= 100; i++) {
			tableDummyData.add(Integer.toString(i));
			commentDummyData.add(Integer.toString(i));
		}
	}

	public static void receiveCommentPost(PageContext pageContext, WebBox callerBox) {
		if (WebBox.isEmptyStr(pageContext.getRequest().getParameter("isCommentSumbit")))
			return;
		String comment = pageContext.getRequest().getParameter("comment");
		if (WebBox.isEmptyStr(comment))
			pageContext.getRequest().setAttribute("errorMSG", "Comment can not be empty");
		else
			commentDummyData.add(0, comment);
	}

	public static class PrepareForDemo6 {
		private int getPageNo(PageContext pageContext, String pageId) {
			String pageNo = (String) pageContext.getRequest().getParameter(pageId + "_pageNo");
			return pageNo == null ? 1 : Integer.valueOf(pageNo);
		}

		public void prepareTable(PageContext pageContext, WebBox callerBox) throws IOException {
			String pageId = callerBox.getAttribute("pageId");
			int pageNo = getPageNo(pageContext, pageId);
			int countPerPage = (Integer) callerBox.getAttribute("row") * (Integer) callerBox.getAttribute("col");
			List<String> targetList = callerBox.getAttribute("targetList");
			ArrayList<String> itemList = new ArrayList<String>();
			for (int i = countPerPage * (pageNo - 1); i < countPerPage * pageNo; i++)
				if (i < targetList.size())
					itemList.add(targetList.get(i));//
			callerBox.setAttribute("itemList", itemList);
		}

		public void preparePaginBar(PageContext pageContext, WebBox callerBox) throws IOException {
			String pageId = callerBox.getAttribute("pageId");
			List<String> targetList = callerBox.getAttribute("targetList");
			int pageNo = getPageNo(pageContext, pageId);
			int countPerPage = (Integer) callerBox.getAttribute("row") * (Integer) callerBox.getAttribute("col");
			int totalPage = (int) Math.ceil(1.0 * targetList.size() / countPerPage);
			callerBox.setAttribute(pageId + "_pageNo", pageNo);
			callerBox.setAttribute("totalPage", totalPage);
		}
	}

	public static class demo6 extends demo1 {
		{
			setAttribute("menu",
					((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo6 - Table & Pagination"));
			List<WebBox> bodyList = new ArrayList<WebBox>();
			bodyList.add(new TableBox());
			bodyList.add(new TablePaginBarBox());
			bodyList.add(new WebBox().setText(
					"<br/>-----------------------------------------------------------------------------------"));
			bodyList.add(new CommentBox());
			bodyList.add(new CommentPaginBarBox());
			bodyList.add(new WebBox("/WEB-INF/pages/commentform.jsp"));
			this.setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".receiveCommentPost");
			this.setAttribute("body", bodyList);
		}

		class TableBox extends WebBox {
			{
				this.setPrepareBean(new PrepareForDemo6()).setPrepareBeanMethod("prepareTable");
				setPage("/WEB-INF/pages/page_table.jsp");
				setAttribute("pageId", "table");
				setAttribute("targetList", tableDummyData);
				setAttribute("row", 3).setAttribute("col", 4);
				setAttribute("render", new WebBox("/WEB-INF/pages/render_table.jsp"));
			}
		}

		class TablePaginBarBox extends TableBox {
			{
				this.setPrepareBean(new PrepareForDemo6()).setPrepareBeanMethod("preparePaginBar");
				setPage("/WEB-INF/pages/pagin_bar.jsp");
			}
		}

		class CommentBox extends TableBox {
			{
				setAttribute("pageId", "comment");
				setAttribute("targetList", commentDummyData);
				setAttribute("row", 3).setAttribute("col", 1);
				setAttribute("render", new WebBox("/WEB-INF/pages/render_comment.jsp"));
			}
		}

		class CommentPaginBarBox extends CommentBox {
			{
				this.setPrepareBean(new PrepareForDemo6()).setPrepareBeanMethod("preparePaginBar");
				setPage("/WEB-INF/pages/pagin_bar.jsp");
			}
		}
	}
}
