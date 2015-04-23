package org.analogweb.servlet.core;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestPath;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class DefaultServletRequestContextTest {

    private DefaultServletRequestContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
    }

    @Test
    public void testGetCookies() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/baa/baz.rn");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("foo", "baa") });
        Cookies.Cookie actual = context.getCookies().getCookie("foo");
        assertThat(actual.getValue(), is("baa"));
    }

    @Test
    public void testPutCookies() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/baa/baz.rn");
        when(request.getContextPath()).thenReturn("/foo");
        when(request.getMethod()).thenReturn("GET");
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("hoge", "fuga") });
        Cookies cookies = context.getCookies();
        cookies.putCookie("foo", "baa");
        Cookies.Cookie cookie = mock(Cookies.Cookie.class);
        when(cookie.getComment()).thenReturn("aComment");
        when(cookie.getDomain()).thenReturn("aDomain");
        when(cookie.getMaxAge()).thenReturn(3600);
        when(cookie.getName()).thenReturn("name");
        when(cookie.getPath()).thenReturn("/");
        when(cookie.getValue()).thenReturn("value");
        when(cookie.getVersion()).thenReturn(1);
        cookies.putCookie(cookie);
        verify(response, times(2)).addCookie(isA(Cookie.class));
    }

    @Test
    public void testGetRequestPath() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(servletContext.getContextPath()).thenReturn("/foo");
        when(request.getRequestURI()).thenReturn("/foo/baa/baz.rn");
        when(request.getMethod()).thenReturn("GET");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("somehost");
        when(request.getServerPort()).thenReturn(80);
        RequestPath actual = context.getRequestPath();
        assertThat(actual.getActualPath(), is("/baa/baz.rn"));
        assertThat(actual.getRequestMethod(), is("GET"));
    }

    @Test
    public void testGetParameters() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getParameterValues("foo")).thenReturn(new String[] { "baa" });
        Parameters actual = context.getQueryParameters();
        assertThat(actual.getValues("foo").get(0), is("baa"));
    }

    @Test
    public void testGetMatrixParameters() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getRequestURI()).thenReturn("/map/color;lat=50;long=20;scale=32000");
        Parameters actual = context.getMatrixParameters();
        assertThat(actual.getValues("long").get(0), is("20"));
    }

    @Test
    public void testRequestHeaders() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getHeaders("foo")).thenReturn(Collections.enumeration(Arrays.asList("baa")));
        Headers actual = context.getRequestHeaders();
        assertThat(actual.getValues("foo").get(0), is("baa"));
    }

    @Test
    public void testRequestBody() throws IOException {
        context = new DefaultServletRequestContext(request, response, servletContext);
        ServletInputStream expected = new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        when(request.getInputStream()).thenReturn(expected);
        InputStream actual = context.getRequestBody();
        assertThat((ServletInputStream) actual, is(expected));
    }

    @Test
    public void testGetContentType() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getHeaders("Content-Type")).thenReturn(
                Collections.enumeration(Arrays.asList("text/xml", "application/xml")));
        MediaType actual = context.getContentType();
        assertThat(actual.getType(), is("text"));
        assertThat(actual.getSubType(), is("xml"));
    }

    @Test
    public void testGetContentTypeWithoutHeaderValue() {
        context = new DefaultServletRequestContext(request, response, servletContext);
        when(request.getHeaders("Content-Type")).thenReturn(
                Collections.enumeration(new ArrayList<String>()));
        MediaType actual = context.getContentType();
        assertThat(actual, is(nullValue()));
    }
}
