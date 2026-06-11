package com.tranyu.ltc.auth;

import jakarta.servlet.http.HttpServletRequest;

public final class LtcUserContextHolder {
    private LtcUserContextHolder() {}

    public static final String REQ_ATTR = LtcUserContextHolder.class.getName() + ".USER";

    public static void set(HttpServletRequest request, LtcUserContext user) {
        request.setAttribute(REQ_ATTR, user);
    }

    public static LtcUserContext get(HttpServletRequest request) {
        Object v = request.getAttribute(REQ_ATTR);
        return v instanceof LtcUserContext u ? u : null;
    }
}
