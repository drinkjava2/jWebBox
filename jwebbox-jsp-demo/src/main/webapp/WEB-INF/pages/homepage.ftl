<#assign box=JspTaglibs["http://github.com/drinkjava2/jwebbox"] />
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
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
 				  <@box.show attribute="menu" />
				</div>
			</div> 
			 	  <@box.show attribute="body" />
			 <div id="temp_footer">
				<div align="center">
				  <@box.show attribute="footer" />
				</div>
			</div>	
		</div>
	</body>
</html>
