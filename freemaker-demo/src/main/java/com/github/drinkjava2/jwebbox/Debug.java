package com.github.drinkjava2.jwebbox;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class is only used to show error and debug information.
 * 
 */
@SuppressWarnings({ "all" })
public class Debug { 

	public static void infosession(HttpSession session) {
		String note = "session ";
		System.out.println(note + "===Session  Begin===");
		for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
			Object o = e.nextElement();
			String name = o.toString();
			// if ((name.indexOf("javax.servlet") == -1) &&
			// (name.indexOf("apache.struts") == -1))
			System.out.println(note + name + "=" + session.getAttribute(o.toString()));
		}
		System.out.println(note + "======end");
	} 

	public static void inforequest(HttpServletRequest request) {
		String note = "request ";
		System.out.println(note + "===Request  Begin===");
		for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
			Object o = e.nextElement();
			String name = o.toString();
			// if ((name.indexOf("javax.servlet") == -1) &&
			// (name.indexOf("apache.struts") == -1))
			{
				String s = note + name + "=";
				try {
					s = s + request.getAttribute(o.toString());
				} catch (Exception ee) {
					s = s + ee.getMessage();
				}
				;
				System.out.println(s);
			}
		}
		System.out.println(note + "======end");
	}
 
	public static void infoparam(HttpServletRequest request) {
		String note = "param ";
		System.out.println(note + "===Param  Begin===");
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			Object o = e.nextElement();
			String name = o.toString();
			if ((name.indexOf("javax.servlet") == -1) && (name.indexOf("apache.struts") == -1))
				System.out.println(note + name + "=" + request.getParameter(o.toString()));
		}
		System.out.println(note + "======end");
	}
 
	public static void infoAttributes(HttpServletRequest request) {
		String note = "Attributes ";
		System.out.println(note + "===Attributes  Begin===");
		try {
			Enumeration e = request.getAttributeNames();
			while (e.hasMoreElements()) {
				String nm = (String) e.nextElement();
				fatal("EMU String=" + nm);
				fatal("EMU Object=" + request.getAttribute(nm));
			}
		} catch (Exception ee) {
			fatal("EMU ERROR!");
		}
		System.out.println(note + "======end");
	}
  
	public static void error(String s) {
		System.out.println(s);
	}

	public static void fatal(String s) { 
		System.out.println(s);
	} 
   

	public static void inforequest2(HttpServletRequest request) {
		System.out.println("Protocol: " + request.getProtocol() + " ");
		System.out.println("Scheme: " + request.getScheme() + " ");
		System.out.println("Server Name: " + request.getServerName() + " ");
		System.out.println("Server Port: " + request.getServerPort() + " ");
		System.out.println("Protocol: " + request.getProtocol() + " ");
		System.out.println("Server Info: " + request.getSession().getServletContext().getServerInfo() + " ");
		System.out.println("Remote Addr: " + request.getRemoteAddr() + " ");
		System.out.println("Remote Host: " + request.getRemoteHost() + " ");
		System.out.println("Character Encoding: " + request.getCharacterEncoding() + " ");
		System.out.println("Content Length: " + request.getContentLength() + " ");
		System.out.println("Content Type: " + request.getContentType() + " ");
		System.out.println("Auth Type: " + request.getAuthType() + " ");
		System.out.println("HTTP Method: " + request.getMethod() + " ");
		System.out.println("Path Info: " + request.getPathInfo() + " ");
		System.out.println("Path Trans: " + request.getPathTranslated() + " ");
		System.out.println("Query String: " + request.getQueryString() + " ");
		System.out.println("Remote User: " + request.getRemoteUser() + " ");
		System.out.println("Session Id: " + request.getRequestedSessionId() + " ");
		System.out.println("Request URI: " + request.getRequestURI() + " ");
		System.out.println("Servlet Path: " + request.getServletPath() + " ");
		System.out.println("Accept: " + request.getHeader("Accept") + " ");
		System.out.println("Host: " + request.getHeader("Host") + " ");
		System.out.println("Referer : " + request.getHeader("Referer") + " ");
		System.out.println("Accept-Language : " + request.getHeader("Accept-Language") + " ");
		System.out.println("Accept-Encoding : " + request.getHeader("Accept-Encoding") + " ");
		System.out.println("User-Agent : " + request.getHeader("User-Agent") + " ");
		System.out.println("Connection : " + request.getHeader("Connection") + " ");
		System.out.println("Cookie : " + request.getHeader("Cookie") + " ");
		System.out.println("Created : " + request.getSession().getCreationTime() + " ");
		System.out.println("LastAccessed : " + request.getSession().getLastAccessedTime() + " ");
	}

	public static void infoRequestAll(HttpServletRequest request) {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("========= Debug request begin ==============");
	
		infosession(request.getSession());
		System.out.println("");
	
		infoparam(request);
		System.out.println("");
	
		inforequest(request);
		System.out.println("");
	
		infoAttributes(request);
		System.out.println("");
	
		inforequest2(request);
		System.out.println("========= Debug request end ==============");
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
 

} 
