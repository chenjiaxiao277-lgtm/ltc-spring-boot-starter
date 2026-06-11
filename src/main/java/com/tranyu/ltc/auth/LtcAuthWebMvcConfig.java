package com.tranyu.ltc.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class LtcAuthWebMvcConfig implements WebMvcConfigurer {
    private final HttpServletRequest request;

    public LtcAuthWebMvcConfig(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LtcCurrentUserArgumentResolver(request));
    }
}
