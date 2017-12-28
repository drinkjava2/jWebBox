(English instruction please see [README-English.md](README-English.md))  
### jWebBox
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
 
这是一个服务端(只支持JSP)页面布局工具，功能和 Apache Tiles类似，特点是简单，仅有300行源码，实现了与Apache Tiles等效的JSP页面布局功能。

#### 目前一些服务端JSP页面布局工具的缺点：
* Apache Tiles: 功能强大但过于臃肿，源码复杂，第三方库引用多，XML配置不方便，运行期动态生成配置能力差。
* Sitemesh: 采用装饰器模式，性能差，功能也不如Tiles强大灵活。  
* JSP Layout或Stripes等JSP布局工具：功能不够强，在布局的继承或参数传递上有问题。

#### JWebBox特点：
1. 简单, 整个项目仅有一个Java类，300行源码，易于学习和扩充。
2. 与jBeanBox和jSqlBox项目类似，用JAVA类代替XML配置（实际上前两个项目是受此项目启发)，支持动态配置，可以在运行期动态生成或修改布局。
3. 无侵入性，可以和其它页面布局工具如Tiles或Sitemesh混用，可用于整个网站的架构，也可用于编写页面局部零件。
4. 支持静态方法、实例方法、URL引用三种数据准备方式。

#### 使用方法：
* 方式1：将WebBox.java源文件拷到项目的源码中即可
* 方式2：在项目的pom.xml中添加如下内容：
```
  <dependency>  
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jwebbox</artifactId>  
    <version>2.1</version>  
  </dependency>
```
jWebBox除了servlet-api和jsp-api这两个Servlet运行期库外(由Servlet容器提供)，没有用到其它第三方库。  

#### 详细介绍
以下通过对示例的解释来详细说明jWebBox的使用，示例项目源码位于jwebbox-demo目录下，在项目的根目录，也有一个打包好的demo.war文件，可直接扔到Tomcat的webapps目录或webLogic的autodeploy目录下运行。

#### 示例1 - 基本布局  
服务端代码如下，这是一个带菜单和底脚的左右布局：
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
其中homepage.jsp是一个模板文件，主要内容如下：
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

说明:  
* setPage方法用于设定当前WebBox实例的目标页面，一个WebBox只能设定一个目标页面，WebBox构造器允许带一个页面参数。 
* setAttribute方法设置WebBox属性，可以为任意Java对象类型，相应地取值用getAttribute方法。
* 在JSP页面中调用WebBox.showAttribute方法，将假定属性值代表一个页面或WebBox实例，并显示它。如果一个页面是由showAttribute方法显示的，用如下方法之一，可以在JSP页面中可以取得它的调用者的属性，例如： 
```
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%>
<%
List<String> itemList = WebBox.getAttribute(pageContext,"itemList")
//或
 WebBox box = WebBox.getBox(pageContext);
 List<String> itemList = box.getAttribute("itemList");
%> 
```

示例1的显示截图：
![image](demo1.png)


#### 示例2 - 布局的继承
服务端代码：
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
DemoTopDown继承于DemoHomePage类，唯一区别是将"body"属性改成了一个上下布局，使用top_down.jsp来作为布局模板，top_down.jsp和left_right.jsp两个布局比较简单，就不在这里列出了，请详见源码。  

示例2的显示截图：   
![image](demo2.png)


#### 示例3 - 数据准备方法
服务端代码：
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
jWebBox支持三种数据准备方式:  
* setPrepareStaticMethod方法指定一个静态方法用于数据准备，注意这个方法必须具有PageContext和WebBox类型的两个参数。
* setPrepareBean方法指定一个对象实例用于数据准备，用setPrepareBeanMethod来指定对象的方法名，如果不指定方法名，将缺省使用"prepare"作为方法名。
* setPrepareURL方法将调用一个URL来作为数据谁备，注意这是一个服务端的URL引用，有权限访问/WEB-INF目录下的内容。
* setText方法用于直接写一段HTML代码输出。

各个方法及页面输出的顺序如下，可以看出，在最终的页面输出之前，可以有4个额外步骤进行数据准备及添加额外的输出内容：  
prepareStaticMethod -> prepareBeanMethod -> URL -> text -> page  

示例3的显示截图：   
![image](demo3.png)


#### 示例4 - 列表
服务端代码：
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
如果属性是一个列表，当JSP页面中调用<% WebBox.showAttribute(pageContext,"body");%>方法时，将假定列表中内容为JSP页面或WebBox实例并依次显示它们。

示例4的显示截图：   
![image](demo4.png)


#### 示例5 - 表格和分页演示
这是一个WebBox各种方法的综合演示，展示利用WebBox来显示两个表格和互不干拢的分页条，以及处理表单提交的数据。因篇幅原因，这个示例只摘录了布局部分代码，其余部分可以查看演示项目源码：
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

示例5的显示截图：   
![image](demo5.png)


以上即为jWebBox的全部说明文档，如有不清楚处，可以查看示例项目的源码。
