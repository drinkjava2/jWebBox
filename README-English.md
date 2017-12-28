### jWebBox
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
 
JWebBox is a small layout tool used in Java server pages(JSP) projects, playing the same role like Tiles and SiteMesh, but it's pure Object-Oriented designed, no XML files, no Tags, it's powerful but simple(only 1 Java class) and easy to use, it can be used to build whole web site or only some page components.

#### Shortage of some other JSP layout tools：
* Apache Tiles: too complicated, too many dependencies, XML configuration is not easy use.
* Sitemesh: not flexible, poor performance.
* JSP Layout or Stripes: not powerful, has problem in extending and parameter transfer.

#### Features of JWebBox:
1. Easy, only 1 Java file, 300 lines source code, easy to study.
2. Use pure Java as configuration, support dynamic configuration, configuration can be built at run time.
3. No invation, can be used together with other tool like Tiles or SiteMesh.
4. Support 3 data prepare methods: static method, bean method, URL.

#### How to use：
* Method 1： Copy WebBox.java source file to project
* Method 2： put below lines in project's pom.xml：
```
  <dependency>  
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jwebbox</artifactId>  
    <version>2.1</version>  
  </dependency>
```
jWebBox has no 3rd party dependencies except servlet-api and jsp-api, which are provided by servlet container.

#### Introduction
There is a demo project shows how to use jWebBox, the demo project located in jwebbox-demo folder, and there is a "demo.war" file can be run on Tomcat or WebLogic.

#### Example 1 - Basic layout
``` 
  public static class DemoHomePage extends WebBox {
    { this.setPage("/WEB-INF/pages/homepage.jsp"); 
      this.setAttribute("menu",
          new WebBox("/WEB-INF/pages/menu.jsp").setAttribute("msg", "Demo1 - A basic layout"));
      this.setAttribute("body", new LeftRightLayout());
      this.setAttribute("footer",  new WebBox( "/WEB-INF/pages/footer.jsp"));
    }
  }
   
  public static class LeftRightLayout extends WebBox {
    { this.setPage("/WEB-INF/pages/left_right.jsp");
      ArrayList<WebBox> boxlist = new ArrayList<WebBox>();
      boxlist.add(new WebBox().setPage("/WEB-INF/pages/page1.jsp"));
      boxlist.add(new WebBox().setPage("/WEB-INF/pages/page2.jsp"));
      this.setAttribute("boxlist", boxlist);
    }
  }  
```
homepage.jsp is a template JSP file:
```
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%> 
<head>...</head>
 <body>
    <div id="temp_content">
      <div id="temp_menu"> 
        <% WebBox.showAttribute(pageContext, "menu");%> 
      </div>
        <% WebBox.showAttribute(pageContext,"body");%>
      <div id="temp_footer"> 
        <% WebBox.showAttribute(pageContext,"footer");%> 
      </div>  
    </div>
 </body>
</html>
```
Explain:  
* Method "setPage" set a target page, 1 WebBox can only set 1 target page, WebBox's constructor allow a page as constructor parameter.
* Method "setAttribute" set a value in WebBox, to get the value, use getAttribute method.
* In JSP page, use WebBox.showAttribute method to show JSP page or WebBox. If a JSP page is shown by showAttribute method, can use below one of below methods to get the value： 
```
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<%
List<String> itemList = WebBox.getAttribute(pageContext,"itemList")
//or
 WebBox box = WebBox.getBox(pageContext);
 List<String> itemList = box.getAttribute("itemList");
%> 
```

Screenshots of example 1：
![image](demo1.png)


#### Example 2 - Extending of layout
```
  public static class DemoTopDown extends DemoHomePage {
    {   ((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo2 - Change body layout");
      this.setAttribute("body", new TopDownLayout());
    }
  }
 
  public static class TopDownLayout extends LeftRightLayout {
    { this.setPage("/WEB-INF/pages/top_down.jsp");
    }
  }
```
DemoTopDown extends DemoHomePage, change "body" to top-down layout.  

Screenshots of example 2： 
![image](demo2.png)


#### Example 3 - Data prepare
```
  public static class DemoPrepareData extends DemoHomePage {
    {  setPrepareStaticMethod(DemoBoxConfig.class.getName() + ".changeMenu");
      setAttribute("body", new WebBox().setText("<div style=\"width:900px\"> This is body </div>")
          .setPrepareURL("/WEB-INF/pages/prepare.jsp").setPrepareBean(new Printer()));
      setAttribute("footer", new WebBox("/WEB-INF/pages/footer.jsp").setPrepareBean(new Printer())
          .setPrepareBeanMethod("print"));
    }
  }

  public static void changeMenu(PageContext pageContext, WebBox callerBox) throws IOException {
    ((WebBox) callerBox.getAttribute("menu")).setAttribute("msg", "Demo3 - Prepare methods");
  }

  public static class Printer {
    public void prepare(PageContext pageContext, WebBox callerBox) throws IOException {
      pageContext.getOut().write("This is printed by Printer's default prepare method <br/>");
    }

    public void print(PageContext pageContext, WebBox callerBox) throws IOException {
      pageContext.getOut().write("This is printed by Printer's print method <br/>");
      pageContext.getOut().write((String) pageContext.getRequest().getAttribute("urlPrepare"));
    }
  }
```
jWebBox supports 3 data prepare methods:
* setPrepareStaticMethod method assign a static method to prepare data.
* setPrepareBean method assign a Bean's to prepare data, setPrepareBeanMethod method set the method name, if not set method name, will use "prepare" as default name.
* setPrepareURL method will call a URL to prepare data, this is a server side call can access files under /WEB-INF folder.
And there is a setText method to set extra html text output.

The order of methods be called and output see below:
prepareStaticMethod -> prepareBeanMethod -> URL -> text -> page  

Screenshots of example 3： 
![image](demo3.png)


#### Example 4 - List
```
  public static class DemoList extends DemoHomePage {
    {
      ((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo4 - List");
      ArrayList<Object> boxlist1 = new ArrayList<Object>();
      for (int i = 1; i <= 3; i++)
        boxlist1.add(new WebBox().setPage("/WEB-INF/pages/page" + i + ".jsp"));
      this.setAttribute("body", boxlist1);
    }
  }
```
If attribute is a list, when use method <% WebBox.showAttribute(pageContext,"body");%> in JSP page, will assume all items in list are page or WebBox instance and show them.

Screenshots of example 4：  
![image](demo4.png)


#### Example5 - Table, pagination, and receive data form post.
This example shows uses jWebBox to create tables and pagination bars, here only shows source code of layouts, for detail please see source code of demo project.
```
  public static class DemoTable extends DemoHomePage {
    {
      setAttribute("menu",
          ((WebBox) this.getAttribute("menu")).setAttribute("msg", "Demo5 - Table & Pagination"));
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
        this.setPrepareBean(new PrepareDemo5()).setPrepareBeanMethod("prepareTable");
        setPage("/WEB-INF/pages/page_table.jsp");
        setAttribute("pageId", "table");
        setAttribute("targetList", tableDummyData);
        setAttribute("row", 3).setAttribute("col", 4);
        setAttribute("render", new WebBox("/WEB-INF/pages/render_table.jsp"));
      }
    }

    class TablePaginBarBox extends TableBox {
      {
        this.setPrepareBean(new PrepareDemo5()).setPrepareBeanMethod("preparePaginBar");
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
        this.setPrepareBean(new PrepareDemo5()).setPrepareBeanMethod("preparePaginBar");
        setPage("/WEB-INF/pages/pagin_bar.jsp");
      }
    }
  }
```

Screenshots of example 5：  
![image](demo5.png)

Above are all documents of jWebBox project, if not clear please see source code of demo project.
