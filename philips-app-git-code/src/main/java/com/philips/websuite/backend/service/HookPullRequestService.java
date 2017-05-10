package com.philips.websuite.backend.service;

import java.net.HttpURLConnection;
import javax.ws.rs.core.Response;

/**
 * @author crhobus
 */
public class HookPullRequestService {

    public Response pullRequest(String json) {
        try {
            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (Exception ex) {
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(ex.getMessage()).build();
        }
    }
}
