(English version see [README-English.md](README-English.md))  
### jWebBox
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
 
这是一个服务端(支持JSP和FreeMaker)页面布局工具，特点是简单，无XML，仅用1000行源码实现了与Apache Tiles类似的页面布局功能。

### 目前一些服务端JSP页面布局工具的缺点：
* Apache Tiles: 功能强大但过于臃肿，源码复杂，第三方库引用多，XML配置不方便，动态配置功能差。
* Sitemesh: 采用装饰器模式，功能不如Apache Tiles灵活。  
* JSP Layout或Stripes等JSP布局工具：功能不够强，在布局的继承或参数传递上有问题。

### JWebBox特点：
1. 简单, 整个项目仅约1千行源码，易于学习和维护。
2. 与jBeanBox和jSqlBox项目类似，用纯JAVA类代替XML配置（实际上前两个项目是受此项目启发)，支持动态配置，配置可以继承，可以在运行期动态生成。
3. 无侵入性，支持JSP、FreeMaker、html、Beetl等各种模板混用。可用于整个网站的服务端布局，也可用于编写页面局部零件。
4. 支持静态方法、实例方法、URL引用三种数据准备方式。
5. 可利用它搭建小巧的MVC架构，无须引入第三方MVC框架，详见[jBooox](https://gitee.com/drinkjava2/jBooox)、GoSqlGo等项目。
  
### 使用方法：
在项目的pom.xml中添加如下内容：
``` 
    <dependency>
      <groupId>com.github.drinkjava2</groupId>
      <artifactId>jwebbox</artifactId>
      <version>3.0</version> <!-- or newest jWebBox -->
    </dependency>   
    
	<!-- 注：如果使用纯html布局，以下两个依赖可以不用添加 -->
    <dependency>
       <groupId>javax.servlet</groupId>
       <artifactId>javax.servlet-api</artifactId>
       <version>4.0.1</version>
	   <scope>provided</scope> 
    </dependency>
 
    <dependency>
      <groupId>javax.servlet.jsp</groupId>
      <artifactId>javax.servlet.jsp-api</artifactId>
      <version>2.3.3</version>
	  <scope>provided</scope>
    </dependency> 
```
jWebBox3.0运行于Java8或以上，如果使用JSP布局，还依赖于javax.servlet-api和javax.servlet.jsp-api这两个运行期库(通常由Servlet容器提供)。  

### 先介绍纯HTML环境下布局的使用，具体示例详见jwebbox-html-demo子目录
采用HTML布局的优点是可以将所有界面生成逻辑放在前端，降低对后端程序员的要求，并且可以在不启动Servlet容器的前提下进行单元测试。  
#### 示例1 - 一个带menu、body、footer的三栏布局配置：
```
	public static class demo1 extends baseBox {
		{   
			setPage("/WEB-INF/pages/layout.htm");
			setAttribute("body", new body());
			setAttribute("footer", new footer());
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
```
对应的html模板为:
```
<!DOCTYPE html>
<html>
<head>
<title>JWebBox Demo</title>  
</head> 
	<body>
		<div id="temp_content">
			<div id="temp_menu">
				<div align="center"> 
 				    $$show(/WEB-INF/pages/menu.htm)
				</div>
			</div> 
			<div align="center">
				    $$show(body)
			</div>  
			<div id="temp_footer">
				<div align="center">
				    $$show(footer) 
				</div>
			</div>	
		</div>
	</body>
</html> 
```
在运行期，在Servlet中调用new demo1().show(request, response);就可以得到最终生成的HTML页面了，

#### 示例2和3 - 布局的继承
服务端代码：
```
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
```
demo2继承于demo1，将"body"属性改成了一个左右布局leftRight。  
demo3继承于demo1，将"body"属性改成了一个上下布局topDown，这个上下布局继承于左右布局，只是更改了模板。  

#### 示例4 - 列表布局
服务端代码：
```
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
```
布局可以在运行期动态生成，如果属性是一个List，将假定列表项目为WebBox对象，会按照列表顺序进行输出。

#### 示例5 - 数据准备方法
服务端代码：
```
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
```
相比与普通JSP的Include指令，WebBox这种布局工具的优势在于可以在各个子页面加载之前调用一些方法。jWebBox有三种数据准备方式:  
* setPrepareStaticMethod方法指定一个静态方法用于数据准备。
* setPrepareBean方法指定一个对象实例用于数据准备，用setPrepareBeanMethod来指定对象的方法名，如果不指定方法名，将缺省使用"prepare"作为方法名。
* setPrepareURL方法将调用一个URL来作为数据谁备，这是一个服务端的URL引用，可以访问/WEB-INF目录下的内容。
另外WebBox还可以用setText方法额外设置一小段文本，将直接作为HTML代码片段插入到子页面前面。

各个准备方法及页面输出的顺序如下：  
prepareStaticMethod -> prepareBeanMethod -> PrepareURL -> text output -> page

#### 示例6 - 其它模板支持，示例源码详见jwebbox-html-demo子目录
WebBox可以支持任意模板，利如以下配置将主页面的body部分用Beetl模板输出：
```
public static class beetlDemo extends demo1 {
		{ 
			setAttribute("body", new beetlPage());
		}
	} 

	public static class beetlPage extends WebBox {
		{
			setPage("/beetl.btl");
		}

		@Override
		public void render(HttpServletRequest request, HttpServletResponse response, String pageOrUrl)
				throws Exception {
			ServletGroupTemplate.instance().render(pageOrUrl, request, response);
		}

	}
```
要支持其它模板，需要重写WebBox类的render方法。通过重写这个方法，可以支持不限类型的模板。重写可只写在根类上，其它的布局继承它即可。  
支持继承是jWebBox布局工具的一个最大的优点。  

### 再介绍JSP环境下布局的使用，详见Demo目录下的jwebbox-jsp-demo目录
JSP环境下布局功能比HTML要强许多，它可以运行嵌入在JSP中的Java，并有许多标签库可以使用。但JSP的缺点是需要Servlet容器支持，不利于单元测试。  
JSP的布局和HTML布局很类似，只是改成了使用JspBox来代替WebBox类，
#### 示例1 - 一个带菜单和底脚的左右布局    
``` 
  public static class demo1 extends JspBox {
    {   this.setPage("/WEB-INF/pages/homepage.jsp");
      this.setAttribute("menu",
          new JspBox("/WEB-INF/pages/menu.jsp").setAttribute("msg", "Demo1 - A basic layout"));
      this.setAttribute("body", new LeftRightLayout());
      this.setAttribute("footer", "/WEB-INF/pages/footer.jsp");
    }
  }

  public static class LeftRightLayout extends JspBox {
    {   this.setPage("/WEB-INF/pages/left_right_layout.jsp");
      ArrayList<Object> boxlist = new ArrayList<Object>();
      boxlist.add("/WEB-INF/pages/page1.jsp");
      boxlist.add("/WEB-INF/pages/page2.jsp");
      this.setAttribute("boxlist", boxlist);
    }
  }
```
其中homepage.jsp是主模板文件，主要内容如下：
```
<%@ taglib prefix="box" uri="http://github.com/drinkjava2/jwebbox"%> 
<html>
  <body>
    <div id="temp_content">
      <div id="temp_menu"> 
            <box:show attribute="menu" /> 
      </div>
             <box:show attribute="body" /> 
       <div id="temp_footer"> 
           <box:show attribute="footer" />  
      </div>  
    </div>
  </body>
</html>
```
left_right_layout.jsp是一个布局模板，内容如下(其它的JSP文件类似，此处略，详见示例)：
```
<%@ taglib prefix="box" uri="http://github.com/drinkjava2/jwebbox"%> 
<div id="temp_left" style="margin: 10px; width: 430px; float: left; background-color:#CCFFCC;"> 
    <box:show target="${jwebbox.attributeMap.boxlist[0]}" />
</div>
<div id="temp_right"  style="margin: 10px; float: right; width: 430px;background-color:#FFFFCC;">
     <box:show target="${jwebbox.attributeMap.boxlist[1]}" />
</div>
```
解释:  
* setPage方法用于设定当前WebBox实例的目标页面(可选)，JspBox构造器允许带一个页面参数。 
* setAttribute方法在JspBox的一个内部HashMap中暂存一个键值，值可以为任意Java对象类型，相应地取值用getAttribute方法，在JSP中可以用EL表达式${jwebbox.attributeMap.keyname}获取。
* 在JSP页面中调用<box:show attribute="body" />标签来显示对应键值的页面，值只能是String、JspBox实例或它们的List。
* show标签的另一个用法是<box:show target="xxx"/>, target只能是String、JspBox或List。如下5种写法在JSP中是等同的:
```
   <box:show attribute="menu" />                                                         
   <box:show target="${jwebbox.attributeMap.menu}" />   
   <% JspBox.showAttribute(pageContext,"menu");%>   
   <% JspBox.showTarget(pageContext, JspBox.getAttribute(pageContext,"menu"));%>           
   <% ((JspBox)JspBox.getAttribute(pageContext,"menu")).show(pageContext);%>  //仅当menu属性为JspBox对象时  
```
后三种写法不推荐，但有助于理解JspBox的运作机制。每个被JspBox调用的页面，都在request中存在一个JspBox实例，可以用request.getAttribute("JSPBOX")或EL表达式${JSPBOX}获取。 
* show标签使用时必须在JSP页面加入TagLib库的引用：<%@ taglib prefix="box" uri="http://github.com/drinkjava2/jwebbox/jspbox"%> 
* 每个JspBox实例，可以设定一个可选的name属性，每个页面用只能获取属于自已的一个JspBox实例，但是可以用getFatherJspBox方法获取当前JspBox实例的调用者所在页面的JspBox实例。
* 在JSP和Servlet中,JspBox可以在页面中动态生成并调用show方法显示，例如:<% new JspBox("/somepage.jsp").setPrepareStaticMethod("xxx").show(pageContext); %>
* 本示例项目中运用了一个小技巧，利用一个Servlet将所有".htm"后缀的访问转化对JspBox的创建和显示，在web.xml中配置如下
```
  <servlet>
    <servlet-name>htm2box</servlet-name>
    <jsp-file>/htm2box.jsp</jsp-file>
  </servlet>

  <servlet-mapping>
    <servlet-name>htm2box</servlet-name>
    <url-pattern>*.htm</url-pattern>
  </servlet-mapping>
```
其中htm2box.jsp当作Servlet来使用，作用类似于Spring MVC中的DispatcherServlet:
```
<%@page import="org.apache.commons.lang.StringUtils"%><%@page import="com.github.drinkjava2.jwebbox.JspBox"%><%
  String uri=StringUtils.substringBefore(request.getRequestURI(),".");
  uri = StringUtils.substringAfterLast(uri, "/");
  if (uri == null || uri.length() == 0)
    uri = "demo1";
  JspBox box = (JspBox) Class.forName("com.github.drinkjava2.jwebboxdemo.DemoBoxConfig$" + uri).newInstance();
  box.show(pageContext);
%>
```
示例1的输出:
![image](demo1.png)

#### 示例2 - 布局的继承
服务端代码：
```
  public static class demo2 extends demo1 {
    {  ((JspBox) this.getAttribute("menu")).setAttribute("msg", "Demo2 - Change body layout");
      this.setAttribute("body", new TopDownLayout());
    }
  }

  public static class TopDownLayout extends LeftRightLayout {
    {  this.setPage("/WEB-INF/pages/top_down_layout.jsp");
    }
  }
```
demo2继承于demo1类，将"body"属性改成了一个上下布局top_down_layout.jsp模板(源码见示例)。  

示例2的输出：   
![image](demo2.png)


#### 示例3 - 数据准备
服务端代码：
```
  public static class demo3 extends demo1 {
    {  setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".changeMenu");
      setAttribute("body", new JspBox().setText("<div style=\"width:900px\"> This is body text </div>")
          .setPrepareURL("/WEB-INF/pages/prepare.jsp").setPrepareBean(new Printer()));
      setAttribute("footer", new JspBox("/WEB-INF/pages/footer.jsp").setPrepareBean(new Printer())
          .setPrepareBeanMethod("print"));
    }
  }

  public static void changeMenu(PageContext pageContext, JspBox callerBox) throws IOException {
    ((JspBox) callerBox.getAttribute("menu")).setAttribute("msg",
        "Demo3 - Prepare methods <br/>This is modified by \"changeMenu\" static method");
  }

  public static class Printer {
    public void prepare(PageContext pageContext, JspBox callerBox) throws IOException {
      pageContext.getOut().write("This is printed by Printer's default \"prepare\" method <br/>");
    }

    public void print(PageContext pageContext, JspBox callerBox) throws IOException {
      pageContext.getOut().write("This is printed by Printer's \"print\" method <br/>");
      pageContext.getOut().write((String) pageContext.getRequest().getAttribute("urlPrepare"));
    }
  }
```
相比与普通的Include指令，Apache Tiles和jWebBox这类布局工具的优势之一在于可以在各个子页面加载之前进行数据准备工作，从而达到模块式开发的目的。jWebBox有三种数据准备方式:  
* setPrepareStaticMethod方法指定一个静态方法用于数据准备。
* setPrepareBean方法指定一个对象实例用于数据准备，用setPrepareBeanMethod来指定对象的方法名，如果不指定方法名，将缺省使用"prepare"作为方法名。
* setPrepareURL方法将调用一个URL来作为数据谁备，这是一个服务端的URL引用，可以访问/WEB-INF目录下的内容。
* setText方法可以额外设置一小段文本，将直接作为HTML代码片段插入到子页面前面。

各个准备方法及页面输出的顺序如下：  
prepareStaticMethod -> prepareBeanMethod -> PrepareURL -> text output -> page

示例3输出：   
![image](demo3.png)


#### 示例4 - 列表
服务端代码：
```
  public static class demo4 extends demo1 {
    {
      ((JspBox) this.getAttribute("menu")).setAttribute("msg", "Demo4 - List");
      ArrayList<Object> child = new ArrayList<Object>();
      for (int i = 1; i <= 3; i++)
        child.add(new JspBox("/WEB-INF/pages/page" + i + ".jsp").setText("&nbsp;&nbsp;&nbsp;&nbsp;"));
      ArrayList<Object> mainList = new ArrayList<Object>();
      for (int i = 1; i <= 3; i++) {
        mainList.add("/WEB-INF/pages/page" + i + ".jsp");
        if (i == 2)
          mainList.add(child);
      }
      this.setAttribute("body", mainList);
    }
  }
```
如果属性是一个列表，当JSP页面中调用<box:show attribute="xxx" />方法时，如果值是一个List,将假定List中属性为页面或JspBox实例并依次显示。  
示例4输出：   
![image](demo4.png)

#### 示例5 - FreeMaker模板支持
从2.1版起,jWebBox开始支持FreeMaker,且可以与JSP混用，例如如下配置：
```
  public static class demo5 extends JspBox {
    {  this.setPage("/WEB-INF/pages/homepage.ftl");
      this.setAttribute("menu",
          new JspBox("/WEB-INF/pages/menu.jsp").setAttribute("msg", "Demo5 - Freemaker demo"));
      this.setAttribute("body", new FreemakerLeftRightLayout());
      this.setAttribute("footer", new JspBox("/WEB-INF/pages/footer.jsp"));
    }
  }
```
FreeMaker不支持直接在页面嵌入Java代码，语法也与JSP不同，引入标签要写成<#assign box=JspTaglibs["http://github.com/drinkjava2/jwebbox"] />, show标签要写成<@box.show attribute="menu" />  
使用FreeMaker,需要在web.xml中添加如下配置：
```
  <servlet>
    <servlet-name>freemarker</servlet-name>
    <servlet-class>freemarker.ext.servlet.FreemarkerServlet</servlet-class>
    <init-param>
      <param-name>TemplatePath</param-name>
      <param-value>/</param-value>
    </init-param>
  </servlet>

  <servlet-mapping>
    <servlet-name>freemarker</servlet-name>
    <url-pattern>*.ftl</url-pattern>
  </servlet-mapping>
```
并在pom.xml中添加对FreeMaker库的依赖：
```  
 <dependency>
     <groupId>org.freemarker</groupId>
     <artifactId>freemarker</artifactId>
     <version>2.3.23</version> <!--或更新版-->
  </dependency>
   
```
示例5输出：   
![image](demo5.png)


#### 示例6 - 表格和分页演示
这个例子展示了利用JspBox配置的继承功能来创建表格和分页条组件，输出两个表格和分页条，并处理表单提交数据。因篇幅较长，此处只摘录布局部分代码：
```
  public static class demo6 extends demo1 {
    {
      setAttribute("menu",
          ((JspBox) this.getAttribute("menu")).setAttribute("msg", "Demo6 - Table & Pagination"));
      List<JspBox> bodyList = new ArrayList<JspBox>();
      bodyList.add(new TableBox());
      bodyList.add(new TablePaginBarBox());
      bodyList.add(new JspBox().setText(
          "<br/>-----------------------------------------------------------------------------------"));
      bodyList.add(new CommentBox());
      bodyList.add(new CommentPaginBarBox());
      bodyList.add(new JspBox("/WEB-INF/pages/commentform.jsp"));
      this.setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".receiveCommentPost");
      this.setAttribute("body", bodyList);
    }

    class TableBox extends JspBox {
      {
        this.setPrepareBean(new PrepareForDemo6()).setPrepareBeanMethod("prepareTable");
        setPage("/WEB-INF/pages/page_table.jsp");
        setAttribute("pageId", "table");
        setAttribute("targetList", tableDummyData);
        setAttribute("row", 3).setAttribute("col", 4);
        setAttribute("render", new JspBox("/WEB-INF/pages/render_table.jsp"));
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
        setAttribute("render", new JspBox("/WEB-INF/pages/render_comment.jsp"));
      }
    }

    class CommentPaginBarBox extends CommentBox {
      {
        this.setPrepareBean(new PrepareForDemo6()).setPrepareBeanMethod("preparePaginBar");
        setPage("/WEB-INF/pages/pagin_bar.jsp");
      }
    }
  }
```

示例6截图：   
![image](demo6.png) 

以上即为jWebBox的全部说明文档，如有不清楚处，可以查看项目源码或示例项目的源码，这是一个很小的项目，只有几个文件。

#### 附录-版本更新记录：
jWebBox2.1 添加FreeMaker模板支持;增加一个JSP标签;添加了表格、分页、表单处理的演示;更正WebLogic不能运行的bug。  
jWebBox2.1.1 添加了beforeShow、beforeexecute、execute、afterExecute、afterShow、afterPrepared几个空方法作为回调函数给子类用。示例详见jBooox项目。  
jWebBox2.1.2 show()方法原来返回为void类型，现改为WebBox实例，便方便使用。  
jWebBox3.0 WebBox改为支持纯HTML布局，原来的JSP布局功能改为使用JspBox实现。  