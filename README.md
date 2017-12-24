#jWebBox
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)  
 
这是一个服务端JSP页面布局工具，功能和 Apache Tiles类似，特点是简单高效，仅有300行源码，实现了与Apache Tiles等效的布局功能。  
在前后端分离的今天，服务端布局还是有必要存在的，因为它减少了客户端与服务端的交互次数，将数据准备方法和页面绑定，可以实现显示层的模块式开发。

### 目前一些JSP服务端页面布局工具的缺点：   
Apache Tiles: 功能强大但过于臃肿，源码复杂，第三方库引用多，XML配置不方便，运行期动态生成配置能力差。
Sitemesh: 采用装饰器模式，性能差，功能也不如Tiles强大灵活。

### JWebBox优点：
1)简单, 整个项目仅有300行源码，易于学习和扩充。
2)与jBeanBox和jSqlBox项目类似，用JAVA类代替XML配置（实际上前两个项目也是受此项目启发, 利用public static类来代替XML可以成为一种模式)，支持动态配置。
3)无侵入性，甚至可以和其它页面布局工具如Tiles或Sitemesh混用，可用于整个网站的架构，也可用于编写页面局部零件。
4)除了servlet-api和jsp-api这两个Servlet运行期库外(由Servlet容器提供)，没有任何其它第三方库依赖。 
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
以下将通过对示例的解释来详细说明jWebBox的使用，示例项目源码位于jwebbox-demo下。在项目的根目录，也可以找到打包好的ROOT.war文件，可直接扔到Tomcat的webapps目录或webLogic的XXXdomain\autodeploy目录下运行。

#### 示例一， 基本布局  

今天要钓鱼去了，回来再接着写。