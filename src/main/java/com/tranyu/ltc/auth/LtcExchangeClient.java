package com.tranyu.ltc.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class LtcExchangeClient {
    private static final Logger log = LoggerFactory.getLogger(LtcExchangeClient.class);

    private final LtcAuthProperties props;
    private final RestClient restClient = RestClient.builder().build();

    public LtcExchangeClient(LtcAuthProperties props) {
        this.props = props;
    }

    public LtcUserContext exchange(String code) {
        String url = props.getExchangeUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("ltc.auth.exchange-url 未配置");
        }
        log.info("[LtcAuth] exchange code={} via {}", code, url);
        ExchangeResp resp;
        try {
            resp = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("code", code))
                    .retrieve()
                    .body(ExchangeResp.class);
        } catch (Exception e) {
            log.error("[LtcAuth] exchange-code 请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 LTC 失败：" + e.getMessage());
        }
        if (resp == null || !Boolean.TRUE.equals(resp.res) || resp.data == null) {
            String err = resp != null && resp.err != null ? resp.err : "无响应";
            throw new RuntimeException(err);
        }
        ExchangeData d = resp.data;
        if (d.targetId == null) throw new RuntimeException("LTC 未返回 targetId");
        return new LtcUserContext(d.targetId, d.deptId, d.userName);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeResp {
        public Boolean res;
        public String err;
        public ExchangeData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeData {
        public Long targetId;
        public String userName;
        public Long deptId;
    }
}
