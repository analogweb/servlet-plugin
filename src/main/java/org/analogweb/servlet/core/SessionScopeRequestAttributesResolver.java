package org.analogweb.servlet.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.analogweb.InvocationMetadata;
import org.analogweb.core.ContextSpecificAttributesHandler;
import org.analogweb.servlet.ServletRequestContext;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class SessionScopeRequestAttributesResolver extends
        ContextSpecificAttributesHandler<ServletRequestContext> {

    private static final Log log = Logs.getLog(SessionScopeRequestAttributesResolver.class);

    @Override
    protected Object resolveAttributeValueOnContext(ServletRequestContext requestContext,
            InvocationMetadata metadatan, String name, Class<?> requiredType) {
        HttpServletRequest request = requestContext.getServletRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(name);
    }

    @Override
    protected void putAttributeValueOnContext(ServletRequestContext requestContext, String name,
            Object value) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        HttpServletRequest request = requestContext.getServletRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001",
                SessionScopeRequestAttributesResolver.class.getCanonicalName(), name, value);
    }

    @Override
    protected void removeAttributeOnContext(ServletRequestContext requestContext, String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        HttpServletRequest request = requestContext.getServletRequest();
        HttpSession session = request.getSession(true);
        session.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002",
                SessionScopeRequestAttributesResolver.class.getCanonicalName(), name);
    }

}
