package org.analogweb.servlet.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.core.DefaultResponseWriter;
import org.analogweb.servlet.core.DefaultServletRequestContext.ServletRequestHeaders;

/**
 * @author snowgoose
 */
public class ServletResponseContext implements ResponseContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private ServletResponseHeaders responseHeaders;
    private ResponseWriter writer;

    public ServletResponseContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    public HttpServletRequest getServletRequest() {
        return request;
    }

    public HttpServletResponse getServletResponse() {
        return response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void commmit(RequestContext context) {
        // nop.
    }

    @Override
    public Headers getResponseHeaders() {
        if (this.responseHeaders == null) {
            this.responseHeaders = new ServletResponseHeaders(getServletRequest(),
                    getServletResponse());
        }
        return this.responseHeaders;
    }

    @Override
    public ResponseWriter getResponseWriter() {
        if(writer == null){
            writer = new DefaultResponseWriter();
        }
        return writer;
    }

    @Override
    public void setStatus(int status) {
        getServletResponse().setStatus(status);
    }

    static class ServletResponseHeaders extends ServletRequestHeaders {

        private HttpServletResponse response;

        ServletResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
            super(request);
            this.response = response;
        }

        @Override
        public void putValue(String name, String value) {
            this.response.addHeader(name, value);
        }

    }
    
}
