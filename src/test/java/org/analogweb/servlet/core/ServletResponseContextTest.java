package org.analogweb.servlet.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.analogweb.Headers;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ServletResponseContextTest {

    private ServletResponseContext context;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext servletContext;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        servletContext = mock(ServletContext.class);
        context = new ServletResponseContext(request, response, servletContext);
    }

    @Test
    public void testGetResponseHeaders() {
        when(request.getHeaders("foo")).thenReturn(Collections.enumeration(Arrays.asList("baa")));

        Headers actual = context.getResponseHeaders();
        assertThat(actual.getValues("foo").get(0), is("baa"));

        actual.putValue("baa", "baz");
        verify(response).addHeader("baa", "baz");
    }

    @Test
    public void testSetResponseCode() {
        context.setStatus(404);
        verify(response).setStatus(404);
    }

}
