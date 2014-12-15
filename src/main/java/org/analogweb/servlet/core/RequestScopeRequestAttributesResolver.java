package org.analogweb.servlet.core;

import javax.servlet.http.HttpServletRequest;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.ContextSpecificAttributesHandler;
import org.analogweb.servlet.ServletRequestContext;
import org.analogweb.util.Assertion;
import org.analogweb.util.RequestContextResolverUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class RequestScopeRequestAttributesResolver extends
        ContextSpecificAttributesHandler<RequestContext> {

    private static final Log log = Logs.getLog(RequestScopeRequestAttributesResolver.class);

    @Override
    protected Object resolveAttributeValueOnContext(RequestContext requestContext,
            InvocationMetadata metadata, String name, Class<?> requiredType) {
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        HttpServletRequest request = s.getServletRequest();
        return request.getAttribute(name);
    }

    @Override
    public void putAttributeValueOnContext(RequestContext requestContext, String name, Object value) {
        Assertion.notNull(requestContext, RequestContext.class.getName());
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        HttpServletRequest request = s.getServletRequest();
        request.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001",
                RequestScopeRequestAttributesResolver.class.getCanonicalName(), name, value);
    }

    @Override
    public void removeAttributeOnContext(RequestContext requestContext, String name) {
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        HttpServletRequest request = s.getServletRequest();
        request.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002",
                RequestScopeRequestAttributesResolver.class.getCanonicalName(), name);
    }
}
