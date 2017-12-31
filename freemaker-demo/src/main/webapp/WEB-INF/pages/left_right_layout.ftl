<#assign box=JspTaglibs["https://github.com/drinkjava2/jwebbox/tld"] />
<div id="temp_left" style="margin: 10px; width: 500px; float: left; background-color:#CCFFCC;">
  <p>${jwebbox.attributeMap.boxlist}
   <#--   @box.include target=jwebbox.boxlist[0] /-->
</div>
<div id="temp_right"  style="margin: 10px; float: right; width: 350px;background-color:#FFFFCC;">
   <#-- @box.include target=jwebbox.boxlist[1] /-->
</div>