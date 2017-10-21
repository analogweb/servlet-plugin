package org.analogweb.servlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.RequestContext;

public interface ServletRequestContext extends RequestContext {

	HttpServletRequest getServletRequest();
	HttpServletResponse getServletResponse();
	ServletContext getServletContext();

}
