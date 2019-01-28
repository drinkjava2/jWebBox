package com.jwebboxdemo;

import java.util.ArrayList;

import com.github.drinkjava2.jwebbox.WebBox;

@SuppressWarnings("all")
public class DemoBoxConfig {

	public static class BaseBox extends WebBox {
		{

		}
	}

	public static class homepage extends BaseBox {
		{
			setPage("/WEB-INF/pages/layout.htm"); 
			setAttribute("body", new body());
			setAttribute("footer", new footer());
		}
	}

	public static class body extends BaseBox {
		{
			setPage("/WEB-INF/pages/body.htm"); 
		}
	}

	public static class footer extends BaseBox {
		{
			setPage("/WEB-INF/pages/footer.htm"); 
		}
	}
	
}
