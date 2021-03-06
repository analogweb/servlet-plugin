package org.analogweb.servlet.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.analogweb.InvocationMetadata;
import org.analogweb.servlet.ServletRequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class SessionScopeRequestAttributesResolverTest {

	private SessionScopeRequestAttributesResolver resolver;
	private ServletRequestContext requestContext;
	private HttpServletRequest request;
	private HttpSession session;
	private InvocationMetadata metadata;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		resolver = new SessionScopeRequestAttributesResolver();
		requestContext = mock(ServletRequestContext.class);
		request = mock(HttpServletRequest.class);
		session = mock(HttpSession.class);
		metadata = mock(InvocationMetadata.class);
	}

	@Test
	public void testResolveAttributeValue() {
		when(requestContext.getServletRequest()).thenReturn(request);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("foo")).thenReturn("baa");
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				null, null);
		assertThat((String) actual, is("baa"));
		verify(session).getAttribute("foo");
	}

	@Test
	public void testResolveAttributeValueAttributeNotAvairable() {
		when(requestContext.getServletRequest()).thenReturn(request);
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("foo")).thenReturn(null);
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				null, null);
		assertNull(actual);
		verify(session).getAttribute("foo");
	}

	@Test
	public void testResolveAttributeValueSessionNotAvairable() {
		when(requestContext.getServletRequest()).thenReturn(request);
		when(request.getSession(false)).thenReturn(null);
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				null, null);
		assertNull(actual);
	}

	@Test
	public void testPutAttributeValue() {
		when(requestContext.getServletRequest()).thenReturn(request);
		when(request.getSession(true)).thenReturn(session);
		doNothing().when(session).setAttribute("foo", "baa");
		resolver.putAttributeValue(requestContext, "foo", "baa");
		verify(session).setAttribute("foo", "baa");
	}

	@Test
	public void testPutAttributeValueWhenNameIsNull() {
		resolver.putAttributeValue(requestContext, null, "baa");
	}

	@Test
	public void testRemoveAttribute() {
		when(requestContext.getServletRequest()).thenReturn(request);
		when(request.getSession(true)).thenReturn(session);
		doNothing().when(session).removeAttribute("baa");
		resolver.removeAttribute(requestContext, "baa");
		verify(session).removeAttribute("baa");
	}

	@Test
	public void testRemoveAttributeWithNullValue() {
		resolver.removeAttribute(requestContext, null);
	}
}
