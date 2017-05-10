package com.philips.websuite.infrastructure.swagger.guice;

import com.google.inject.servlet.ServletModule;
import com.philips.microservices.common.expression.UserExpressionGateway;
import io.swagger.jaxrs.config.BeanConfig;

/**
 * @author crhobus
 */
public class SwaggerModule extends ServletModule {

    @Override
    protected void configureServlets() {

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setDescription(UserExpressionGateway.getTextExpression(768367L));
        beanConfig.setTitle("Web Suite");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("http://localhost:8084");
        beanConfig.setBasePath(getServletContext().getContextPath());
        beanConfig.setResourcePackage("com.philips.websuite.infrastructure.swagger, com.philips.websuite.backend.people");
        beanConfig.setScan(true);

        bind(BeanConfig.class).toInstance(beanConfig);

    }
}
