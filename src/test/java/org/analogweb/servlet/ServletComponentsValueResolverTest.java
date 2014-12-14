package org.analogweb.servlet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.analogweb.InvocationMetadata;
import org.analogweb.core.RequestContextWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletComponentsValueResolverTest {

    private ServletComponentsValueResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new ServletComponentsValueResolver();
    }

    @Test
    public void testGetRequestViaContext() {
        ServletRequestContext original = mock(ServletRequestContext.class);
        HttpServletRequest value = mock(HttpServletRequest.class);
        when(original.getServletRequest()).thenReturn(value);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        Object actual = resolver
                .resolveValue(original, metadata, "", HttpServletRequest.class, null);
        assertThat((HttpServletRequest) actual, is(value));
    }

    @Test
    public void testGetRequest() {
        ServletRequestContext original = mock(ServletRequestContext.class);
        HttpServletRequest value = mock(HttpServletRequest.class);
        when(original.getServletRequest()).thenReturn(value);
        RequestContextWrapper request = new RequestContextWrapper(original);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        Object actual = resolver
                .resolveValue(request, metadata, "", HttpServletRequest.class, null);
        assertThat((HttpServletRequest) actual, is(value));
    }

    @Test
    public void testGetContext() {
        ServletRequestContext original = mock(ServletRequestContext.class);
        ServletContext value = mock(ServletContext.class);
        when(original.getServletContext()).thenReturn(value);
        RequestContextWrapper request = new RequestContextWrapper(original);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        Object actual = resolver.resolveValue(request, metadata, "", ServletContext.class, null);
        assertThat((ServletContext) actual, is(value));
    }

    @Test
    public void testGetSession() {
        ServletRequestContext original = mock(ServletRequestContext.class);
        HttpServletRequest value = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(value.getSession(true)).thenReturn(session);
        when(original.getServletRequest()).thenReturn(value);
        RequestContextWrapper request = new RequestContextWrapper(original);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        Object actual = resolver.resolveValue(request, metadata, "", HttpSession.class, null);
        assertThat((HttpSession) actual, is(session));
    }
}
