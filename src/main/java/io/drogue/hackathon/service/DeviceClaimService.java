package io.drogue.hackathon.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.drogue.hackathon.model.Devices;

@ApplicationScoped
public class DeviceClaimService {
    public static class DeviceClaim {
        public String deviceId;
    }

    public static class AlreadyClaimedException extends WebApplicationException {
        public AlreadyClaimedException(final String deviceId) {
            super(String.format("Device %s is already claimed", deviceId), Response.Status.CONFLICT);
        }
    }

    @Inject
    EntityManager em;

    @Transactional
    public Optional<DeviceClaim> getDeviceClaimFor(final String userId) {
        var cb = em.getCriteriaBuilder();
        var cr = cb.createQuery(Devices.class);
        var root = cr.from(Devices.class);
        cr.select(root).where(cb.equal(root.get("claimedBy"), userId));

        return em.createQuery(cr).getResultStream().findFirst().map(device -> {
            var result = new DeviceClaim();
            result.deviceId = device.getId();
            return result;
        });
    }

    @Transactional
    public void claimDevice(final String deviceId, final String userId, final boolean canCreate) throws AlreadyClaimedException {
        var device = this.em.find(Devices.class, deviceId);
        if (device == null || device.getClaimedBy() != null) {
            if (device == null && canCreate) {
                device = new Devices();
                device.setId(deviceId);
            } else {
                throw new AlreadyClaimedException(deviceId);
            }
        }

        device.setClaimedBy(userId);
        this.em.persist(device);
    }

    @Transactional
    public boolean releaseDevice(final String deviceId, final String userId) {
        var device = this.em.find(Devices.class, deviceId);
        if (device != null && device.getClaimedBy() != null && device.getClaimedBy().equals(userId)) {
            device.setClaimedBy(null);
            this.em.persist(device);
            return true;
        } else {
            return false;
        }
    }
}
