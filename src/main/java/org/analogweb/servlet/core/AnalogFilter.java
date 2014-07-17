package org.analogweb.servlet.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.MissingRequiredParameterException;
import org.analogweb.core.WebApplication;
import org.analogweb.WebApplicationException;
import org.analogweb.util.ApplicationPropertiesHolder;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * TODO write test case.
 * 
 * @author snowgoose
 */
public class AnalogFilter implements Filter {

	private static final Log log = Logs.getLog(AnalogFilter.class);
	private Application webApplication;
	private ApplicationProperties props;
	private ServletContext servletContext;

	@Override
	public void destroy() {
		this.props = null;
		this.webApplication.dispose();
		this.webApplication = null;
		this.servletContext = null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		RequestContext context = createRequestContext(getServletContext(),
				request, response);
		ResponseContext responseContext = createResponseContext(
				getServletContext(), request, response);
		RequestPath requestedPath = context.getRequestPath();
		log.log(Markers.LIFECYCLE, "DL000002", requestedPath);
		try {
			int proceeded = webApplication.processRequest(requestedPath,
					context, responseContext);
			if (proceeded == Application.NOT_FOUND) {
				log.log(Markers.LIFECYCLE, "DL000003", requestedPath);
				chain.doFilter(request, response);
				return;
			}
			ResponseEntity entity = responseContext.getResponseWriter()
					.getEntity();
			if (entity == null) {
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
				return;
			}
			response.setContentLength((int) entity.getContentLength());
			entity.writeInto(response.getOutputStream());
		} catch (WebApplicationException e) {
			Throwable t = e.getCause();
			if (t != null) {
				throw new ServletException(e.getCause());
			} else {
				throw new ServletException(e);
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.servletContext = filterConfig.getServletContext();
		ClassLoader classLoader = getCurrentClassLoader();
		this.webApplication = createApplication(filterConfig, classLoader);
		this.props = configureApplicationProperties(filterConfig);
		this.webApplication.run(createApplicationContext(this.servletContext),
				props, getClassCollectors(), classLoader);
	}

	protected List<ClassCollector> getClassCollectors() {
		List<ClassCollector> list = new ArrayList<ClassCollector>();
		list.add(new JarClassCollector());
		list.add(new FileClassCollector());
		return Collections.unmodifiableList(list);
	}

	protected ApplicationProperties configureApplicationProperties(
			final FilterConfig filterConfig) {
		String packageNames = filterConfig
				.getInitParameter(Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES);
		if (StringUtils.isEmpty(packageNames)) {
			throw new MissingRequiredParameterException(
					Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES);
		}
		return ApplicationPropertiesHolder
				.configure(
						this.webApplication,
						DefaultApplicationProperties.properties(
								filterConfig
										.getInitParameter(Application.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES),
								filterConfig
										.getInitParameter(Application.INIT_PARAMETER_APPLICATION_TEMPORARY_DIR),
								filterConfig
										.getInitParameter(Application.INIT_PARAMETER_APPLICATION_PROVISION_LOCALE)));
	}

	protected RequestContext createRequestContext(ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {
		return new DefaultServletRequestContext(request, response, context);
	}

	protected ResponseContext createResponseContext(
			ServletContext servletContext, HttpServletRequest request,
			HttpServletResponse response) {
		return new ServletResponseContext(request, response, servletContext);
	}

	protected ApplicationContext createApplicationContext(ServletContext context) {
		return new ServletContextApplicationContext(context);
	}

	protected Application createApplication(FilterConfig config,
			ClassLoader classLoader) {
		return new WebApplication();
	}

	protected ClassLoader getCurrentClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	protected final ServletContext getServletContext() {
		return this.servletContext;
	}
}
