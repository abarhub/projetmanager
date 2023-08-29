package org.projetmanager.projetmanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class BitBucketAccesToken {

    @JsonProperty("access_token")
    private String accessToken;
    private String scopes;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    private String state;
    @JsonProperty("refresh_token")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BitBucketAccesToken.class.getSimpleName() + "[", "]")
                .add("accessToken='" + accessToken + "'")
                .add("scopes='" + scopes + "'")
                .add("tokenType='" + tokenType + "'")
                .add("expiresIn=" + expiresIn)
                .add("state='" + state + "'")
                .add("refreshToken='" + refreshToken + "'")
                .toString();
    }
}
