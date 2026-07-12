package com.gymnaughy.android.network.dto;

public class SessionRequest {
    private final String displayName;
    private final String email;

    public SessionRequest(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }
}
