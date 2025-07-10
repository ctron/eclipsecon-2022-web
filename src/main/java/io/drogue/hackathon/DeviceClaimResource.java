package io.drogue.hackathon;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.drogue.hackathon.service.DeviceClaimService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;

@Path("/api/v1alpha1/deviceClaims")
@Authenticated
public class DeviceClaimResource {

    private static final Logger logger = LoggerFactory.getLogger(DeviceClaimResource.class);

    private static class HelloResponse {
        public String id;
    }

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityIdentity identity;

    @Inject
    DeviceClaimService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<DeviceClaimService.DeviceClaim> getDeviceClaim() {
        return this.service.getDeviceClaimFor(this.jwt.getSubject());
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void claimDevice(@QueryParam("deviceId") final String deviceId) {
        var canCreate = this.identity.hasRole("device-admin");
        this.service.claimDevice(deviceId, this.jwt.getSubject(), canCreate);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean releaseDevice(@QueryParam("deviceId") final String deviceId) {
        var result =  this.service.releaseDevice(deviceId, this.jwt.getSubject());
        logger.info("Released device '{}' => {}", deviceId, result);
        return result;
    }
}
