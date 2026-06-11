package com.tranyu.ltc.inner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LtcInnerProperties.class)
public class LtcInnerAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ltc.inner", name = "url")
    public LtcInnerClient ltcInnerClient(LtcInnerProperties props) {
        return new LtcInnerClient(props);
    }

    @Bean
    @ConditionalOnBean({LtcInnerClient.class, LtcUserSyncHandler.class})
    public LtcUserSyncService ltcUserSyncService(LtcInnerClient client, LtcUserSyncHandler handler) {
        return new LtcUserSyncService(client, handler);
    }
}
