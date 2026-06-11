package com.tranyu.ltc.auth;

import java.lang.annotation.*;

/**
 * Controller 参数注入注解。
 * 用法：public Result foo(@CurrentUser LtcUserContext user) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
