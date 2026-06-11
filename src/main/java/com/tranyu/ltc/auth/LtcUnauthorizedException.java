package com.tranyu.ltc.auth;

public class LtcUnauthorizedException extends RuntimeException {
    public LtcUnauthorizedException(String message) {
        super(message);
    }
}
