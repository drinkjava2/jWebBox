JWebBox is a small layout tool used in java server pages(JSP) projects, playing the same role like Tiles and SiteMesh, but it's pure Object-Oriented designed, no XML files, no Tags, it's simple(only 1 java class) and easy to use, it can be used to build whole web site or only some page components. JWebBox2.0.0 is an open source software follows BSD license.  

Key features about JWebBox2.0.0:
1)Use Java class, not XML file to present JSP layouts, the Java class be called "Box".  
2)No conflict with other framework tools like Tiles/Struts/Spring MVC..., it can be used to build whole web site or only for few page components.
3)No XML files, no Tags, no 3rd party jar library needed.
4)All layout are created dynamically, it's easy to modify or create layouts at run-time.
5)Only 1 file "Box.java" is necessary, no .jar file can be found in this project because I recommend you can copy "Box.java" into your source code folder, so you can read and modify source code easily, in fact it's a small file only ~300 lines.
6)JWebBox2.0.0 is based on JWebBox1.x.1 but made lots improvements, old version JWebBox1.x.x is not recommended to use in projects.  

How to use it:
You can throw the jwebbox2.war file into webapps folder, start your Tomcat or JBoss to see the demo, source code is included in jwebbox2.war, this file can also be imported into Eclipse as a project.
  

Typically a project using JWebBox2.0, need prepare below files:

1.JSP page and JSP Template files, for example:
template.jsp:
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="com.jwebbox.Box"%> 
<%Box box = Box.getBox(pageContext);%>
<head>
<style type="text/css">...</style>
<title>JWebBox2.0.0 Demo</title> 
</head>
	<body>
		<div id="temp_content">
			<div id="temp_top">
				<div align="center">
				<% box.showAttribute("menu");%>
				</div>
			</div>
				<% box.showAttribute("body");%>
			<div id="temp_bottom">
				<div align="center">
				<% box.showAttribute("footer");%>
				</div>
			</div>	
		</div>
	</body>
</html>

2.Layout Java classes like:
DemoBox1.java 
public class DemoBox1 extends Box {
	{
		this.setPage("/template/template.jsp");
		this.setAttribute("menu", new Box().setPage("/template/menu.jsp").setAttribute("msg", "Demo1 - A basic layout"));
		this.setAttribute("body", new BodyLeftRight());
		this.setAttribute("footer", "/template/footer.jsp");
	}
}

and DemoBox2.java
public class DemoBox2 extends DemoBox1 {
	{
		this.setAttribute("menu", ((Box) this.getAttribute("menu")).setAttribute("msg", "Demo2 - Change body layout"));
		this.setAttribute("body", new BodyTopDown());
	}
}

BodyLeftRight and BodyTopDown are layout classes, detail see demo.

3.Logic classes, to prepare data to view, create or modify layouts
public class DemoLogic {
 
	public static void prepareStaticMethod1(PageContext pageContext, Box callerBox) throws IOException {
		pageContext.getOut().write("  This is inserted by prepareStaticMethod1 method<br/>");
	}

	public void preparerBeanMethod1(PageContext pageContext, Box callerBox) throws IOException {
		((Box) callerBox.getAttribute("menu")).setAttribute("msg", "Demo3 - Show how prepare methods be called");
		callerBox.setAttribute("footer", "/template/page3.jsp");
	}


	public static DemoLogic getInstance(PageContext pageContext) {
		return SingletonInstance.INSTANCE;	// If use Spring, get bean instance from pageContext:
	}
}

4.At last, call Box.show method on JSP page to show it.
index.jsp：
<%
	String demono = request.getParameter("demono");
	if ("2".equals(demono))
		new DemoBox2().show(pageContext);
	else if ("1".equals(demono))
		new DemoBox1().show(pageContext); 
%>



Yong Zhu
Yong9981@gmail.com
2016-02-06
