package org.analogweb.servlet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.annotation.As;
import org.analogweb.annotation.Resolver;

/**
 * Expose Servlet modules with {@link ServletConponentsValueResolver}.
 * @author snowgooseyk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Resolver(ServletConponentsValueResolver.class)
@As
public @interface ServletModule {
}
