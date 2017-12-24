<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@page import="com.github.drinkjava2.jwebbox.WebBox"%> 
<head>
<style type="text/css">
#temp_content {width: 900px; margin: auto; background-color:#EEEEEE;} 
#temp_content #temp_menu {margin: 10px;background-color:#99CCCC;} 
#temp_content #temp_footer {clear: both; margin: 10px;background-color:#9999CC;} 
</style>
<title>JWebBox2.1 Demo</title> 
</head>
	<body>
		<div id="temp_content">
			<div id="temp_menu">
				<div align="center">
				<% WebBox.showAttribute(pageContext, "menu");%>
				</div>
			</div>
				<% WebBox.showAttribute(pageContext,"body");%>
			<div id="temp_footer">
				<div align="center">
				<% WebBox.showAttribute(pageContext,"footer");%>
				</div>
			</div>	
		</div>
	</body>
</html>
