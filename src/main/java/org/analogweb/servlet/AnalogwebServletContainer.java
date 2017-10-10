package org.analogweb.servlet;

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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseEntity;
import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.DefaultWritableBuffer;
import org.analogweb.core.WebApplication;
import org.analogweb.WebApplicationException;
import org.analogweb.servlet.core.DefaultServletRequestContext;
import org.analogweb.servlet.core.ServletContextApplicationContext;
import org.analogweb.servlet.core.ServletResponseContext;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * Analogweb {@link Application} running on Servlet environment.
 * This container describe as {@link Filter} or {@link HttpServlet}.
 * @author y2k2mt
 */
public class AnalogwebServletContainer extends HttpServlet implements Filter {

    private static final long serialVersionUID = 1053712905786459619L;
    private static final Log log = Logs.getLog(AnalogwebServletContainer.class);
    private Application webApplication;
    private ApplicationProperties props;
    private FilterConfig filterConfig;

    @Override
    public void destroy() {
        super.destroy();
        this.props = null;
        this.webApplication.dispose();
        this.webApplication = null;
        this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (process(req, res) == Application.NOT_FOUND) {
            chain.doFilter(req, res);
            return;
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        if (process(req, res) == Application.NOT_FOUND) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected Response process(ServletRequest req, ServletResponse res) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        RequestContext context = createRequestContext(getServletContext(), request, response);
        ResponseContext responseContext = createResponseContext(getServletContext(), request,
                response);
        RequestPath requestedPath = context.getRequestPath();
        log.log(Markers.LIFECYCLE, "DL000002", requestedPath);
        try {
            Response proceeded = webApplication.processRequest(requestedPath, context, responseContext);
            if (proceeded == Application.NOT_FOUND) {
                log.log(Markers.LIFECYCLE, "DL000003", requestedPath);
                return Application.NOT_FOUND;
            }
            ResponseEntity entity = proceeded.getEntity();
            if (entity == null) {
                return proceeded;
            }
            response.setContentLength((int) entity.getContentLength());
            entity.writeInto(DefaultWritableBuffer.writeBuffer(response.getOutputStream()));
            return proceeded;
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
        this.filterConfig = filterConfig;
        ClassLoader classLoader = getCurrentClassLoader();
        this.webApplication = createApplication(classLoader);
        this.props = configureApplicationProperties(filterConfig);
        this.webApplication.run(createApplicationContext(getServletContext()), props,
                getClassCollectors(), classLoader);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        ClassLoader classLoader = getCurrentClassLoader();
        ServletContext sc = getServletContext();
        this.webApplication = createApplication(classLoader);
        this.props = configureApplicationProperties(sc);
        this.webApplication.run(createApplicationContext(getServletContext()), props,
                getClassCollectors(), classLoader);
    }

    protected List<ClassCollector> getClassCollectors() {
        List<ClassCollector> list = new ArrayList<ClassCollector>();
        list.add(new JarClassCollector());
        list.add(new FileClassCollector());
        return Collections.unmodifiableList(list);
    }

    protected ApplicationProperties configureApplicationProperties(final FilterConfig filterConfig) {
        return DefaultApplicationProperties.properties(
                filterConfig.getInitParameter(ApplicationProperties.PACKAGES),
                filterConfig.getInitParameter(ApplicationProperties.TEMP_DIR),
                filterConfig.getInitParameter(ApplicationProperties.LOCALE));
    }

    protected ApplicationProperties configureApplicationProperties(final ServletContext context) {
        return DefaultApplicationProperties.properties(
                context.getInitParameter(ApplicationProperties.PACKAGES),
                context.getInitParameter(ApplicationProperties.TEMP_DIR),
                context.getInitParameter(ApplicationProperties.LOCALE));
    }

    protected RequestContext createRequestContext(ServletContext context,
            HttpServletRequest request, HttpServletResponse response) {
        return new DefaultServletRequestContext(request, response, context);
    }

    protected ResponseContext createResponseContext(ServletContext servletContext,
            HttpServletRequest request, HttpServletResponse response) {
        return new ServletResponseContext(request, response, servletContext);
    }

    protected ApplicationContext createApplicationContext(ServletContext context) {
        return new ServletContextApplicationContext(context);
    }

    protected Application createApplication(ClassLoader classLoader) {
        return new WebApplication();
    }

    protected ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        return super.getServletContext();
    }
}
