package org.analogweb.servlet.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.analogweb.InvocationMetadata;
import org.analogweb.core.AssertionFailureException;
import org.analogweb.servlet.ServletRequestContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class RequestScopeRequestAttributesResolverTest {

    private RequestScopeRequestAttributesResolver resolver;
    private ServletRequestContext requestContext;
    private HttpServletRequest request;
    private InvocationMetadata metadata;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        resolver = new RequestScopeRequestAttributesResolver();
        requestContext = mock(ServletRequestContext.class);
        request = mock(HttpServletRequest.class);
        metadata = mock(InvocationMetadata.class);
    }

    @Test
    public void testResolveAttributeValue() {
        when(requestContext.getServletRequest()).thenReturn(request);
        when(request.getAttribute("foo")).thenReturn(1L);
        Object actual = resolver.resolveValue(requestContext, metadata, "foo", null, null);
        assertThat((Long) actual, is(1L));
    }

    @Test
    public void testPutAttributeValue() {
        when(requestContext.getServletRequest()).thenReturn(request);
        doNothing().when(request).setAttribute("foo", 1L);
        resolver.putAttributeValue(requestContext, "foo", 1L);
        verify(request).setAttribute("foo", 1L);
    }

    @Test
    public void testPutAttributeValueWithNullContext() {
        thrown.expect(AssertionFailureException.class);
        resolver.putAttributeValue(null, "foo", 1L);
    }

    @Test
    public void testRemoveAttribute() {
        when(requestContext.getServletRequest()).thenReturn(request);
        doNothing().when(request).removeAttribute("boo");
        resolver.removeAttribute(requestContext, "boo");
        verify(request).removeAttribute("boo");
    }
}
