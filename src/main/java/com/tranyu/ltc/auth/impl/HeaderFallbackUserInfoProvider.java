package com.tranyu.ltc.auth.impl;

import com.tranyu.ltc.auth.LtcUserContext;
import com.tranyu.ltc.auth.LtcUserInfoProvider;

public class HeaderFallbackUserInfoProvider implements LtcUserInfoProvider {
    @Override
    public LtcUserContext loadByVt(String vt, String token) {
        return null;
    }
}
