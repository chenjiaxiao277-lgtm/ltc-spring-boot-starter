package com.tranyu.ltc.auth;

/**
 * 将 LTC vt 解析为用户信息的扩展点，由接入方实现（或使用内置的 LtcMeUserInfoProvider）。
 */
public interface LtcUserInfoProvider {
    /**
     * @param vt    LTC 登录态
     * @param token 已签名 token
     * @return 用户上下文；返回 null 表示无法解析（视为未登录）
     */
    LtcUserContext loadByVt(String vt, String token);
}
