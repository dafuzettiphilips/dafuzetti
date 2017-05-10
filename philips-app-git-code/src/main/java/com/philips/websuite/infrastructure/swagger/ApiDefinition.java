package com.philips.websuite.infrastructure.swagger;

import com.philips.microservices.common.expression.UserExpressionGateway;
import com.philips.microservices.common.resources.ServiceDiscovery;
import com.philips.microservices.common.resources.ServiceEnum;
import com.philips.security.commons.consts.OAuth2;
import com.philips.websuite.infrastructure.constant.AuthorizationFlag;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.auth.OAuth2Definition;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.reflections.Reflections;

/**
 * @author crhobus
 */
@Path("")
public class ApiDefinition implements ReaderListener {

    /**
     * @param reader
     * @param swagger
     * @param method
     */
    private void addMethodAuthorizationsToSwagger(Reader reader, Swagger swagger, Method method) {

        Operation methodOperation = null;
        RequiresPermissions permission = null;

        try {
            methodOperation = reader.parseMethod(method);
            permission = method.getAnnotation(RequiresPermissions.class);

            if (permission != null) {
                for (io.swagger.models.Path path : swagger.getPaths().values()) {
                    for (Operation operation : path.getOperationMap().values()) {
                        if (operation.getOperationId().equals(methodOperation.getOperationId())) {

                            operation.addSecurity(OAuth2.ID, Arrays.asList(permission.value()));
                            operation.getParameters().clear();

                            for (io.swagger.models.parameters.Parameter docParam : methodOperation.getParameters()) {
                                operation.addParameter(docParam);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String message = UserExpressionGateway.getTextExpression(768330L)
                    .concat(methodOperation == null ? "" : methodOperation.getDescription())
                    .concat(UserExpressionGateway.getTextExpression(295536L))
                    .concat(permission == null ? "" : permission.toString());

            Logger.getLogger(ApiDefinition.class.getName()).log(Level.INFO, message, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.swagger.jaxrs.config.ReaderListener#beforeScan(io.swagger.jaxrs.Reader
     * , io.swagger.models.Swagger)
     */
    @Override
    public void beforeScan(Reader reader, Swagger swagger) {

        OAuth2Definition oAuth2Definition = new OAuth2Definition().password(
                ServiceDiscovery.getServiceURI(ServiceEnum.PHILIPS_MS_SECURITY) + "/resources/oauth/tokens");

        SortedMap<String, String> oAuth2scopes = new TreeMap<>();
        oAuth2scopes.put(AuthorizationFlag.Permission.LOGIN_RETRIEVE, "It allows reading the login information.");
        oAuth2Definition.setScopes(oAuth2scopes);

        swagger.addSecurityDefinition(OAuth2.ID, oAuth2Definition);
    }

    /**
     * (non-Javadoc)
     *
     * @see ReaderListener#afterScan(Reader, Swagger)
     */
    @Override
    public void afterScan(Reader reader, Swagger swagger) {

        Reflections reflections = new Reflections("com.philips.websuite.backend.api");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Path.class);

        for (Class<?> endpointClass : classes) {
            for (Method method : endpointClass.getMethods()) {
                addMethodAuthorizationsToSwagger(reader, swagger, method);
            }
        }
    }

}
