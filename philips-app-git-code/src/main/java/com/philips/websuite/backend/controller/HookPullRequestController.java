package com.philips.websuite.backend.controller;

import com.google.inject.Inject;
import com.philips.websuite.backend.api.HookPullRequestAPI;
import com.philips.websuite.backend.service.HookPullRequestService;
import javax.ws.rs.core.Response;

/**
 * @author crhobus
 */
public class HookPullRequestController implements HookPullRequestAPI {

    @Inject
    private HookPullRequestService service;

    @Override
    public Response pullRequest(String json) {
        return service.pullRequest(json);
    }
}
