package org.analogweb.servlet.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.analogweb.InvocationMetadata;
import org.analogweb.servlet.ServletRequestContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ApplicationScopeRequestAttributesResolverTest {

	private ApplicationScopeRequestAttributesResolver resolver;
	private ServletRequestContext requestContext;
	private ServletContext servletContext;
	private InvocationMetadata metadata;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		resolver = new ApplicationScopeRequestAttributesResolver();
		requestContext = mock(ServletRequestContext.class);
		servletContext = mock(ServletContext.class);
		metadata = mock(InvocationMetadata.class);
	}

	@Test
	public void testResolveAttributeValue() {
		Object expected = new Object();
		when(requestContext.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute("foo")).thenReturn(expected);
		Object actual = resolver.resolveValue(requestContext, metadata, "foo",
				String.class, null);
		assertThat(actual, is(expected));
		verify(servletContext).getAttribute("foo");
	}

	@Test
	public void testResolveAttributeValueWithNullName() {
		Object actual = resolver.resolveValue(requestContext, metadata, null,
				String.class, null);
		assertNull(actual);
	}

	@Test
	public void testPutAttributeValue() {
		when(requestContext.getServletContext()).thenReturn(servletContext);
		doNothing().when(servletContext).setAttribute("foo", "baa");
		resolver.putAttributeValue(requestContext, "foo", "baa");
		verify(servletContext).setAttribute("foo", "baa");
	}

	@Test
	public void testPutAttributeValueWithNullName() {
		resolver.putAttributeValue(requestContext, null, "baa");
	}

	@Test
	public void testRemoveAttributeValue() {
		when(requestContext.getServletContext()).thenReturn(servletContext);
		resolver.removeAttribute(requestContext, "foo");
		verify(servletContext).removeAttribute("foo");
	}

	@Test
	public void testRemoveAttributeValueWithNullName() {
		resolver.removeAttribute(requestContext, null);
	}
}
