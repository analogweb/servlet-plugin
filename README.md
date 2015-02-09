Analog Web Frameworki Servlet Plugin
===============================================

This plugin enable to run Analogweb's Route on Servlet 3+ container. 

Integrate with Servlet container
----------------------------------------------

In Servlet environment, you have to declear the Anaogweb Application in your web.xml.

```xml
<web-app>

  <servlet>
    <servlet-name>Analogweb</servlet-name>
    <servlet-class>org.analogweb.servlet.AnalogwebServletContainer</servlet-class>
    <init-param>
      <param-name>analogweb.packages</param-name>
      <param-value>com.foo.baa,org.hoge.fuga</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>Analogweb</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>

</web-app>
```

you can also declear the Analogweb Application as a Filter.

```xml
<web-app>

  <filter>
    <filter-name>Analogweb</filter-name>
    <filter-class>org.analogweb.servlet.AnalogwebServletContainer</filter-class>
    <init-param>
      <param-name>analogweb.packages</param-name>
      <param-value>com.foo.baa,org.hoge.fuga</param-value>
    </init-param>
  </filter>

  <filter-mapping>
    <filter-name>Analogweb</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

</web-app>
```
