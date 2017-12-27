# jWebBox
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
 
这是一个服务端JSP页面布局工具，功能和 Apache Tiles类似，特点是简单高效，仅有300行源码，实现了与Apache Tiles等效的布局功能。  
在前后端分离的今天，服务端布局还是有必要存在的，因为它将数据准备和显示页面绑定，可以象搭积木一样实现显示层的模块式开发。  

### 目前一些JSP服务端页面布局工具的缺点：
Apache Tiles: 功能强大但过于臃肿，源码复杂，第三方库引用多，XML配置不方便，运行期动态生成配置能力差。  
Sitemesh: 采用装饰器模式，性能差，功能也不如Tiles强大灵活。  

### JWebBox优点：
1)简单, 整个项目仅有300行源码，易于学习和扩充。  
2)与jBeanBox和jSqlBox项目类似，用JAVA类代替XML配置（实际上前两个项目也是受此项目启发)，支持动态配置。  
3)无侵入性，可以和其它页面布局工具如Tiles或Sitemesh混用，可用于整个网站的架构，也可用于编写页面局部零件。  
4)除了servlet-api和jsp-api这两个Servlet运行期库外(由Servlet容器提供)，没有其它第三方库依赖。  
5)支持静态方法、实例方法、URL引用三种数据准备方式。  

### 使用方法：
方式1：将WebBox.java源文件拷到项目的源码中即可  
方式2：在项目的pom.xml中添加如下内容：
```
  <dependency>  
    <groupId>com.github.drinkjava2</groupId>
    <artifactId>jwebbox</artifactId>  
    <version>2.1</version>  
  </dependency>
```

### 详细介绍
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
* setAttribute方法用于向WebBox实例压入一个属性，可以为任意Java对象类型。  
* 在JSP页面中调用showAttribute方法，将取出属性，如果是"/"开头的字符串表示的页面或一个WebBox实例，将显示它。 
 
示例1的页面截图如下：  
![image](demo1.png)

#### 示例2 - 布局的继承
服务端代码如下：
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

示例2的页面截图如下：  
![image](demo2.png)

#### 示例3 - 数据准备方法
服务端代码如下：
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
* setPrepareBean方法指定一个对象实列用于数据准备，用setPrepareBeanMethod来指定对象的方法名，如果不指定，将缺省使用"prepare"作为方法名。
* setPrepareURL方法将调用一个URL来作为数据谁备，注意这是一个服务端的URL引用，有权限访问/WEB-INF目录下的内容。
* setText方法用于直接写一段HTML代码输出。

各种准备方法及页面显示的顺序依次如下：  
1. 静态方法prepareStaticMethod
2. 实例方法prepareBeanMethod
3. URL引用
4. setText方法
5. page页面

示例3的页面截图如下：  
![image](demo3.png)

#### 示例4 - 列表
服务端代码如下：
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
如果属性是一个列表，当JSP页面中用<% WebBox.showAttribute(pageContext,"body");%>方法调用时，将假定列表中内容为JSP页面或WebBox实例并依次显示它们。

示例4的页面截图如下：  
![image](demo4.png)

#### 示例5 - 表格和分页演示
这是一个WebBox各种方法的综合演示，展示利用WebBox来实现两个表格的显示和分页，以及处理表单提交的内容，这个示例比较长，只摘录了布局部分的代码，有兴趣的可以查看演示项目的源码：
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

示例5的页面截图如下：  
![image](demo4.png)


以上即为jWebBox的全部说明文档，作为一个源码只有300行的小项目，这个说明文档也算是够长的了。