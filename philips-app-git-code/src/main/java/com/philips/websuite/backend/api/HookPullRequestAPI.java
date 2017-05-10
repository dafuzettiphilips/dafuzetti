package com.philips.websuite.backend.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.HttpURLConnection;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author crhobus
 */
@Path("hookPullRequest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "hookPullRequest", tags = {"hookPullRequest"}, protocols = "http")
public interface HookPullRequestAPI {

    @POST
    @ApiOperation(httpMethod = "POST",
            code = HttpURLConnection.HTTP_OK,
            value = "", responseContainer = "")
    Response pullRequest(String json);

}
