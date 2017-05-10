package com.philips.websuite.infrastructure;

import com.google.inject.servlet.ServletModule;
import com.philips.app.commons.scanner.ReflectionsFactory;
import com.philips.app.commons.scanner.ScannerFactory;
import com.philips.app.data.DataModule;
import com.philips.app.jaxrs.guice.ResteasyModule;
import com.philips.security.client.guice.modules.SecurityModule;
import com.philips.websuite.infrastructure.database.DatabaseProvider;
import com.philips.websuite.infrastructure.swagger.guice.SwaggerModule;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

/**
 * @author crhobus
 */
public class GuiceModule extends ServletModule {

    private static final ScannerFactory RF = new ReflectionsFactory("com.philips", "io.swagger.jaxrs.listing");

    @Override
    protected void configureServlets() {

        super.configureServlets();

        /**
         * Security module binds a filter that should be the first filter in the
         * servlet's filter chain, right after the Guice's filter.
         *
         * Therefore, tit must be kept as the first installed module.
         */
        install(new SecurityModule(Path.class));

        /**
         * Other modules
         */
        install(new SwaggerModule());

        /**
         * Reastesy does not chain down the received requests, due to this
         * behaviour, it must be kept as the last installed module.
         */
        ResteasyModule resteasyModule = new ResteasyModule(RF);
        resteasyModule
                .withCors()
                .allowMethod(HttpMethod.GET)
                .allowOrigin("*");

        install(resteasyModule);

        install(new DataModule(RF, new DatabaseProvider()));
    }
}
