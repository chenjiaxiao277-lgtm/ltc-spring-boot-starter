package com.tranyu.ltc.auth.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tranyu.ltc.auth.LtcAuthProperties;
import com.tranyu.ltc.auth.LtcUserContext;
import com.tranyu.ltc.auth.LtcUserInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@ConditionalOnExpression("!'${ltc.auth.me-url:}'.trim().isEmpty()")
public class LtcMeUserInfoProvider implements LtcUserInfoProvider {
    private final LtcAuthProperties props;
    private final RestClient ltcMeRestClient;

    public LtcMeUserInfoProvider(LtcAuthProperties props, RestClient ltcMeRestClient) {
        this.props = props;
        this.ltcMeRestClient = ltcMeRestClient;
    }

    @Override
    public LtcUserContext loadByVt(String vt, String token) {
        try {
            MeResp resp = ltcMeRestClient.post()
                    .uri(props.getMeUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(props.getVtHeader(), vt)
                    .header(props.getTokenHeader(), token)
                    .body("{}")
                    .retrieve()
                    .body(MeResp.class);

            if (resp == null || !Boolean.TRUE.equals(resp.res) || resp.data == null) return null;
            MeData d = resp.data;
            if (d.id == null) return null;

            Long deptId = null;
            if (d.organIdList != null && !d.organIdList.isEmpty()) {
                deptId = d.organIdList.get(0).department;
            }
            return new LtcUserContext(d.id, deptId, d.userName);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeResp {
        public Boolean res;
        public MeData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeData {
        public Long id;
        public String userName;
        public List<OrganItem> organIdList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrganItem {
        public Long department;
    }
}
