package org.analogweb.servlet.core;

import javax.servlet.ServletContext;

import org.analogweb.InvocationMetadata;
import org.analogweb.core.ContextSpecificAttributesHandler;
import org.analogweb.servlet.ServletRequestContext;
import org.analogweb.util.RequestContextResolverUtils;
import org.analogweb.util.StringUtils;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class ApplicationScopeRequestAttributesResolver extends
        ContextSpecificAttributesHandler<ServletRequestContext> {

    private static final Log log = Logs.getLog(ApplicationScopeRequestAttributesResolver.class);

    @Override
    protected Object resolveAttributeValueOnContext(ServletRequestContext requestContext,
            InvocationMetadata metadata, String key, Class<?> requiredType) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        ServletContext servletContext = s.getServletContext();
        return servletContext.getAttribute(key);
    }

    @Override
    protected void putAttributeValueOnContext(ServletRequestContext requestContext, String name,
            Object value) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        ServletContext servletContext = s.getServletContext();
        servletContext.setAttribute(name, value);
        log.log(Markers.VARIABLE_ACCESS, "TV000001",
                ApplicationScopeRequestAttributesResolver.class.getCanonicalName(), name, value);
    }

    @Override
    protected void removeAttributeOnContext(ServletRequestContext requestContext, String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        ServletRequestContext s = RequestContextResolverUtils.resolveRequestContext(requestContext);
        ServletContext servletContext = s.getServletContext();
        servletContext.removeAttribute(name);
        log.log(Markers.VARIABLE_ACCESS, "TV000002",
                ApplicationScopeRequestAttributesResolver.class.getCanonicalName(), name);
    }
}
