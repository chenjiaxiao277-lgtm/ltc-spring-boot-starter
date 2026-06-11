package com.tranyu.ltc.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ltc.auth")
public class LtcAuthProperties {
    private boolean enabled = false;
    private String vtHeader = "vt";
    private String tokenHeader = "token";
    private String meUrl;
    private int meTimeoutMs = 3000;
    private String exchangeUrl;
    private boolean allowFallbackHeader = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getVtHeader() { return vtHeader; }
    public void setVtHeader(String vtHeader) { this.vtHeader = vtHeader; }
    public String getTokenHeader() { return tokenHeader; }
    public void setTokenHeader(String tokenHeader) { this.tokenHeader = tokenHeader; }
    public String getMeUrl() { return meUrl; }
    public void setMeUrl(String meUrl) { this.meUrl = meUrl; }
    public int getMeTimeoutMs() { return meTimeoutMs; }
    public void setMeTimeoutMs(int meTimeoutMs) { this.meTimeoutMs = meTimeoutMs; }
    public String getExchangeUrl() { return exchangeUrl; }
    public void setExchangeUrl(String exchangeUrl) { this.exchangeUrl = exchangeUrl; }
    public boolean isAllowFallbackHeader() { return allowFallbackHeader; }
    public void setAllowFallbackHeader(boolean allowFallbackHeader) { this.allowFallbackHeader = allowFallbackHeader; }
}
