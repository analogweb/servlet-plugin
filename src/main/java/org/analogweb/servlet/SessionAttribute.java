package org.analogweb.servlet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.annotation.As;
import org.analogweb.annotation.Resolver;
import org.analogweb.servlet.core.SessionScopeRequestAttributesResolver;

/**
 * Handle attribute value via {@link SessionScopeRequestAttributesResolver}.
 * 
 * @author y2k2mt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Resolver(SessionScopeRequestAttributesResolver.class)
@As
public @interface SessionAttribute {
}
