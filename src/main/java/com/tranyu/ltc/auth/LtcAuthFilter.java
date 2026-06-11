package com.tranyu.ltc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

public class LtcAuthFilter extends OncePerRequestFilter {
    private final LtcAuthProperties props;
    private final LtcSignatureVerifier signatureVerifier;
    private final LtcUserInfoProvider userInfoProvider;
    private final ObjectMapper objectMapper;

    public LtcAuthFilter(LtcAuthProperties props, LtcSignatureVerifier signatureVerifier,
                         LtcUserInfoProvider userInfoProvider, ObjectMapper objectMapper) {
        this.props = props;
        this.signatureVerifier = signatureVerifier;
        this.userInfoProvider = userInfoProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.contains("/auth/callback")) return true;
        String auth = request.getHeader("Authorization");
        return auth != null && auth.startsWith("Bearer ");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!props.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String vt    = request.getHeader(props.getVtHeader());
        String token = request.getHeader(props.getTokenHeader());

        if (!signatureVerifier.verify(vt, token)) {
            if (props.isAllowFallbackHeader()) {
                filterChain.doFilter(request, response);
                return;
            }
            write401(response, "未登录（vt/token 校验失败）");
            return;
        }

        LtcUserContext user = userInfoProvider.loadByVt(vt, token);
        if (user == null || user.getTargetId() == null) {
            write401(response, "未登录（无法从 vt 获取用户）");
            return;
        }

        LtcUserContextHolder.set(request, user);
        filterChain.doFilter(request, response);
    }

    private void write401(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(401);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                objectMapper.writeValueAsString(Map.of("code", 401, "message", msg)));
    }
}
