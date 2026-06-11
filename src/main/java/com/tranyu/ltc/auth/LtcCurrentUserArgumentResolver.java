package com.tranyu.ltc.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LtcCurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final HttpServletRequest request;

    public LtcCurrentUserArgumentResolver(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && LtcUserContext.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        LtcUserContext user = LtcUserContextHolder.get(request);
        if (user == null || user.getTargetId() == null) {
            throw new LtcUnauthorizedException("未登录（缺少 LTC 登录态）");
        }
        return user;
    }
}
