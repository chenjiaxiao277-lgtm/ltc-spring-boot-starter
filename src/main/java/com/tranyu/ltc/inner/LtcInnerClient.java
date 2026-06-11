package com.tranyu.ltc.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

public class LtcInnerClient {
    private final LtcInnerProperties props;
    private final RestClient restClient = RestClient.builder().build();

    public LtcInnerClient(LtcInnerProperties props) {
        this.props = props;
    }

    public List<LtcInnerUser> listUsers() {
        if (props.getUrl() == null || props.getUrl().isBlank()) {
            throw new IllegalStateException("ltc.inner.url 未配置");
        }
        if (props.getSecret() == null || props.getSecret().isBlank()) {
            throw new IllegalStateException("ltc.inner.secret 未配置");
        }
        InnerResp resp = restClient.post()
                .uri(props.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Inner-Secret", props.getSecret())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (resp == null || !Boolean.TRUE.equals(resp.res) || resp.data == null) {
            throw new RuntimeException("LTC 用户同步失败：" + (resp != null ? "res=false" : "无响应"));
        }
        return resp.data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InnerResp {
        public Boolean res;
        public List<LtcInnerUser> data;
    }
}
