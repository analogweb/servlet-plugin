package org.analogweb.servlet.core;

import javax.servlet.ServletContext;

import org.analogweb.ApplicationContext;

/**
 * {@link ServletContext}よりアプリケーションスコープのコンポーネントを 解決する
 * {@link ApplicationContextResolver}の実装です。
 * 
 * @author snowgoose
 */
public class ServletContextApplicationContext implements ApplicationContext {

	private final ServletContext context;

	public ServletContextApplicationContext(ServletContext context) {
		this.context = context;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(Class<T> requiredType, String contextKey) {
		Object value = getContext().getAttribute(contextKey);
		if (requiredType.isInstance(value)) {
			return (T) value;
		}
		return null;
	}

	protected final ServletContext getContext() {
		return this.context;
	}
}
