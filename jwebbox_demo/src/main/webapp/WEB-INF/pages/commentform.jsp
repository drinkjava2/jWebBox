<div style="700px; clear:both;float:right; text-align:center;font-size:12px;padding-right:110px;">
	  <form id="cmform" name="cmform" method="post" action="/demo/5" >
		<table style="width:650px;"> 
		<tr>
			<td style="width:120px;">
				<div>
					&nbsp;
				</div>
			</td> 		
			<td valign="middle"> 
  				<div style="color:red">
					<ul>
					<%
						String errorMSG=(String)request.getAttribute("errorMSG");
									  if(errorMSG!=null)
										  out.write(errorMSG); 
					%>
					</ul>
				</div>
			</td>
		</tr>			
		<tr>
			<td valign="top">
				<div align="right">
					Add comment:
				</div>
			</td> 
			<td valign="top">
				<div align="left">
					<textarea id="comment" name="comment" style="width:500px;height:50px;font-size:12px;"></textarea>
					<input type="hidden" name="isCommentSumbit" value="Y" />
				</div>
			</td>
		</tr> 
		<tr>
			<td colspan="2" valign="middle">&nbsp;
				<div align="center">
					<input style="width:70px" type="submit" name="Submit" value="Submit" />
					</div>
			</td>
		</tr>		
		</table>
		</form>
</div>