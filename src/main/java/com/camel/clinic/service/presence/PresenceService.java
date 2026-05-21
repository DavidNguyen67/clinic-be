package com.camel.clinic.service.presence;

public interface PresenceService {
    void setOnline(String userId);

    void setOffline(String userId);

    boolean isOnline(String userId);
}