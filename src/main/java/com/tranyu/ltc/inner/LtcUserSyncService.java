package com.tranyu.ltc.inner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LtcUserSyncService {
    private static final Logger log = LoggerFactory.getLogger(LtcUserSyncService.class);

    private final LtcInnerClient innerClient;
    private final LtcUserSyncHandler handler;

    public LtcUserSyncService(LtcInnerClient innerClient, LtcUserSyncHandler handler) {
        this.innerClient = innerClient;
        this.handler = handler;
    }

    public void sync() {
        log.info("[LtcSync] 开始从 LTC 同步用户...");
        List<LtcInnerUser> users = innerClient.listUsers();
        log.info("[LtcSync] 获取到 {} 个用户，交由 handler 处理", users.size());
        handler.onUserList(users);
        log.info("[LtcSync] 同步完成");
    }
}
