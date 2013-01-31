package org.analogweb.servlet.core;

import javax.servlet.http.HttpServletRequest;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.core.ContextSpecificAttributesHandler;
import org.analogweb.servlet.ServletRequestContext;
import org.analogweb.util.Assertion;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class RequestScopeRequestAttributesResolver extends
        ContextSpecificAttributesHandler<ServletRequestContext> {

    private static final Log log = Logs.getLog(RequestScopeRequestAttributesResolver.class);

    @Override
    public String getScopeName() {
        return "request";
    }

    @Override
    protected Object resolveAttributeValueOnContext(ServletRequestContext requestContext,
            InvocationMetadata metadata, String name, Class<?> requiredType) {
        HttpServletRequest request = requestContext.getServletRequest();
        return request.getAttribute(name);
    }

    @Override
    public void putAttributeValueOnContext(ServletRequestContext requestContext, String name,
            Object value) {
        Assertion.notNull(requestContext, RequestContext.class.getName());
        HttpServletRequest request = requestContext.getServletRequest();
        request.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001", getScopeName(), name, value);
    }

    @Override
    public void removeAttributeOnContext(ServletRequestContext requestContext, String name) {
        HttpServletRequest request = requestContext.getServletRequest();
        request.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002", getScopeName(), name);
    }

}
