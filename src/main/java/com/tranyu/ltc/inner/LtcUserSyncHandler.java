package com.tranyu.ltc.inner;

import java.util.List;

/**
 * 人员同步扩展点：接入方实现此接口，决定如何将 LTC 用户列表存入自己的数据库。
 *
 * 示例：
 * <pre>
 * {@code
 * @Component
 * public class MyUserSyncHandler implements LtcUserSyncHandler {
 *     @Override
 *     public void onUserList(List<LtcInnerUser> users) {
 *         for (LtcInnerUser u : users) {
 *             // upsert 到自己的用户表
 *         }
 *     }
 * }
 * }
 * </pre>
 */
public interface LtcUserSyncHandler {
    void onUserList(List<LtcInnerUser> users);
}
