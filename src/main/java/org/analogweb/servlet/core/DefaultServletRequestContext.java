package org.analogweb.servlet.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestPath;
import org.analogweb.core.DefaultRequestPath;
import org.analogweb.core.MatrixParameters;
import org.analogweb.core.MediaTypes;
import org.analogweb.servlet.ServletRequestContext;
import org.analogweb.util.ArrayUtils;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class DefaultServletRequestContext implements ServletRequestContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ServletContext servletContext;
    private Cookies cookies;
    private ServletRequestHeaders requestHeaders;
    private ServletParameters parameters;
    private Parameters matrixParameters;

    public DefaultServletRequestContext(HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
    }

    @Override
    public HttpServletRequest getServletRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getServletResponse() {
        return response;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public RequestPath getRequestPath() {
        HttpServletRequest request = getServletRequest();
        return new DefaultRequestPath(URI.create(getServletContext().getContextPath()),
                URI.create(request.getRequestURI()), request.getMethod());
    }

    @Override
    public Cookies getCookies() {
        if (this.cookies == null) {
            this.cookies = new ServletCookies(getServletRequest(), getServletResponse());
        }
        return this.cookies;
    }

    static class ServletCookies implements Cookies {

        private Map<String, Cookies.Cookie> cookieMap;
        private HttpServletResponse response;

        ServletCookies(HttpServletRequest request, HttpServletResponse response) {
            this.cookieMap = Maps.newEmptyHashMap();
            for (javax.servlet.http.Cookie c : request.getCookies()) {
                this.cookieMap.put(c.getName(), new ServletCookie(c));
            }
            this.response = response;
        }

        @Override
        public Cookie getCookie(String name) {
            return this.cookieMap.get(name);
        }

        @Override
        public void putCookie(String name, Object value) {
            this.response.addCookie(new javax.servlet.http.Cookie(name, value.toString()));
        }

        @Override
        public void putCookie(Cookie cookie) {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(cookie.getName(),
                    cookie.getValue());
            c.setComment(cookie.getComment());
            c.setDomain(cookie.getDomain());
            c.setMaxAge(cookie.getMaxAge());
            c.setPath(cookie.getPath());
            c.setSecure(cookie.isSecure());
            c.setVersion(cookie.getVersion());
            this.response.addCookie(c);
        }
    }

    static class ServletCookie implements Cookies.Cookie {

        private javax.servlet.http.Cookie cookie;

        ServletCookie(javax.servlet.http.Cookie cookie) {
            this.cookie = cookie;
        }

        @Override
        public String getName() {
            return this.cookie.getName();
        }

        @Override
        public String getValue() {
            return this.cookie.getValue();
        }

        @Override
        public String getComment() {
            return this.cookie.getComment();
        }

        @Override
        public String getPath() {
            return this.cookie.getPath();
        }

        @Override
        public int getMaxAge() {
            return this.cookie.getMaxAge();
        }

        @Override
        public boolean isSecure() {
            return this.cookie.getSecure();
        }

        @Override
        public int getVersion() {
            return this.cookie.getVersion();
        }

        @Override
        public String getDomain() {
            return this.cookie.getDomain();
        }
    }

    @Override
    public Headers getRequestHeaders() {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ServletRequestHeaders(getServletRequest());
        }
        return this.requestHeaders;
    }

    static class ServletRequestHeaders implements Headers {

        private HttpServletRequest request;

        ServletRequestHeaders(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public List<String> getValues(String name) {
            return Collections.list(this.request.getHeaders(name));
        }

        @Override
        public List<String> getNames() {
            return Collections.list(this.request.getHeaderNames());
        }

        @Override
        public void putValue(String name, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(String name) {
            return this.request.getHeader(name) != null;
        }
    }

    @Override
    public Parameters getQueryParameters() {
        if (this.parameters == null) {
            this.parameters = new ServletParameters(getServletRequest());
        }
        return this.parameters;
    }

    @Override
    public Parameters getMatrixParameters() {
        if (this.matrixParameters == null) {
            this.matrixParameters = new MatrixParameters(URI.create(getServletRequest()
                    .getRequestURI()));
        }
        return this.matrixParameters;
    }

    @Override
    public Parameters getFormParameters() {
        return getQueryParameters();
    }

    static class ServletParameters implements Parameters {

        private HttpServletRequest request;

        ServletParameters(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public List<String> getValues(String key) {
            String[] values = request.getParameterValues(key);
            if (ArrayUtils.isEmpty(values)) {
                return Arrays.asList(request.getParameter(key));
            }
            return Arrays.asList(values);
        }

        @Override
        public Map<String, String[]> asMap() {
            return request.getParameterMap();
        }
    }

    @Override
    public InputStream getRequestBody() throws IOException {
        return getServletRequest().getInputStream();
    }

    @Override
    public MediaType getContentType() {
        List<String> contentTypes = getRequestHeaders().getValues("Content-Type");
        if (contentTypes.isEmpty()) {
            return null;
        }
        return MediaTypes.valueOf(contentTypes.get(0));
    }

    @Override
    public Locale getLocale() {
        return getServletRequest().getLocale();
    }

    @Override
    public List<Locale> getLocales() {
        return Collections.list(getServletRequest().getLocales());
    }
}
