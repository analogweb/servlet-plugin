package org.analogweb.servlet;

import java.lang.annotation.Annotation;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.core.RequestContextWrapper;

/**
 * @author snowgooseyk
 */
public class ServletConponentsValueResolver implements RequestValueResolver {

    @Override
    public Object resolveValue(RequestContext request, InvocationMetadata metadata, String query,
            Class<?> requiredType, Annotation[] parameterAnnotations) {
        ServletRequestContext src = resolveRequestContext(request);
        if (src == null) {
            return null;
        }
        if (ServletRequest.class.isAssignableFrom(requiredType)) {
            return src.getServletRequest();
        } else if (HttpSession.class.isAssignableFrom(requiredType)) {
            return src.getServletRequest().getSession(true);
        } else if (ServletContext.class.isAssignableFrom(requiredType)) {
            return src.getServletContext();
        }
        return null;
    }

    private ServletRequestContext resolveRequestContext(RequestContext request) {
        if (request instanceof ServletRequestContext) {
            return (ServletRequestContext) request;
        } else if (request instanceof RequestContextWrapper) {
            RequestContextWrapper rcw = (RequestContextWrapper) request;
            if (rcw.getOriginalRequestContext() instanceof ServletRequestContext) {
                return (ServletRequestContext) rcw.getOriginalRequestContext();
            }
        }
        return null;
    }
}
