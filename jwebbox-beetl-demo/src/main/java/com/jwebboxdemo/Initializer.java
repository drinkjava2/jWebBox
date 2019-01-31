package com.jwebboxdemo;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.ext.servlet.ServletGroupTemplate;

import com.github.drinkjava2.jwebbox.WebBox;
import com.github.drinkjava2.jwebbox.WebBox.WebBoxException;

public class Initializer implements ServletContextListener {
	 
	@Override
	public void contextInitialized(ServletContextEvent event) {
		String root = WebBox.getWebappFolder() + "/WEB-INF/pages";
		FileResourceLoader resourceLoader = new FileResourceLoader(root, "utf-8");
		Configuration cfg;
		try {
			cfg = Configuration.defaultConfiguration();
		} catch (IOException e) {
			throw new WebBoxException(e);
		} 
		
		GroupTemplate g=ServletGroupTemplate.instance().getGroupTemplate();
		g.setConf(cfg);
		g.setResourceLoader(resourceLoader);
	}
}
