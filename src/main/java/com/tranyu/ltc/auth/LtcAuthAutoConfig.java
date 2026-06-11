package com.tranyu.ltc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tranyu.ltc.auth.impl.HeaderFallbackUserInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestClient;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableConfigurationProperties(LtcAuthProperties.class)
public class LtcAuthAutoConfig {

    @Bean
    public LtcSignatureVerifier ltcSignatureVerifier() {
        return new LtcSignatureVerifier();
    }

    @Bean
    public LtcExchangeClient ltcExchangeClient(LtcAuthProperties props) {
        return new LtcExchangeClient(props);
    }

    @Bean
    public LtcAuthWebMvcConfig ltcAuthWebMvcConfig(HttpServletRequest request) {
        return new LtcAuthWebMvcConfig(request);
    }

    /** 配置了 me-url 时创建专用 RestClient */
    @Bean
    @ConditionalOnExpression("!'${ltc.auth.me-url:}'.trim().isEmpty()")
    public RestClient ltcMeRestClient() {
        return RestClient.builder().build();
    }

    /** 未提供自定义实现时用兜底（返回 null，配合 allowFallbackHeader 使用） */
    @Bean
    @ConditionalOnMissingBean(LtcUserInfoProvider.class)
    public LtcUserInfoProvider ltcUserInfoProvider() {
        return new HeaderFallbackUserInfoProvider();
    }

    /** ltc.auth.enabled=true 时注册拦截 Filter */
    @Bean
    @ConditionalOnProperty(prefix = "ltc.auth", name = "enabled", havingValue = "true")
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    public FilterRegistrationBean<LtcAuthFilter> ltcAuthFilterRegistration(
            LtcAuthProperties props,
            LtcSignatureVerifier verifier,
            LtcUserInfoProvider provider,
            ObjectMapper objectMapper) {
        FilterRegistrationBean<LtcAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new LtcAuthFilter(props, verifier, provider, objectMapper));
        reg.addUrlPatterns("/api/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return reg;
    }
}
