# ltc-spring-boot-starter

LTC 统一登录鉴权 + 人员同步 Spring Boot Starter。

接入后开箱即用：
- Controller 参数注入当前登录用户（`@CurrentUser`）
- vt/token 签名自动验证
- LTC code 换用户信息（扫码登录回调）
- 从 LTC 拉取全量人员并同步到业务表

---

## 环境要求

- Java 17+
- Spring Boot 3.x

---

## 第一步：获取 JAR

暂无私服，需本地 install。

```bash
git clone https://github.com/chenjiaxiao277-lgtm/ltc-spring-boot-starter.git
cd ltc-spring-boot-starter
mvn clean install -DskipTests
```

执行成功后 JAR 安装到本地 `~/.m2`，后续在任意项目中都可以引用。

---

## 第二步：在业务项目中添加依赖

在你的项目 `pom.xml` 中加入：

```xml
<dependency>
    <groupId>com.tranyu</groupId>
    <artifactId>ltc-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 第三步：配置 application.yml

```yaml
ltc:
  auth:
    # 是否启用 LTC 鉴权（true = 生产/测试环境；false = 纯本地调试）
    enabled: true

    # LTC 扫码登录回调：用 code 换取用户信息
    exchange-url: https://ltc.tranyu.com/ltc/v1.0/auth/exchange-code

    # LTC 用户中心：通过 vt/token 获取当前登录用户
    me-url: https://ltc.tranyu.com/ltc/v1.0/customer/me

    # vt/token 校验失败时是否允许继续（本地开发设 true，生产环境设 false）
    allow-fallback-header: false

  inner:
    # LTC 内部人员列表接口
    url: https://ltc.tranyu.com/ltc/v1.0/inner/user/list
    # 内部接口鉴权 secret（向 LTC 侧获取）
    secret: your-inner-secret
```

> **本地开发免登录配置**：将 `ltc.auth.enabled` 设为 `false`，或设为 `true` + `allow-fallback-header: true`，
> 然后在请求头里带 `X-User-Id: 你的用户ID` 即可绕过 LTC 验证。

---

## 第四步：实现人员同步（存到业务表）

Starter 提供了 `LtcUserSyncHandler` 接口，你来决定如何把 LTC 用户存到自己的数据库。

在你的项目里新建一个实现类：

```java
import com.tranyu.ltc.inner.LtcInnerUser;
import com.tranyu.ltc.inner.LtcUserSyncHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSyncHandler implements LtcUserSyncHandler {

    // 注入你自己的 UserRepository / Mapper
    private final UserRepository userRepository;

    public UserSyncHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onUserList(List<LtcInnerUser> users) {
        for (LtcInnerUser u : users) {
            // u.getTargetId()  — 用户 ID（LTC targetId）
            // u.getUserName()  — 姓名
            // u.getMobile()    — 手机号
            // u.getDeptId()    — 部门 ID
            // u.getUnionId()   — 飞书 union_id（用于飞书消息推送）

            // 示例：upsert 到本地用户表
            userRepository.upsert(u.getTargetId(), u.getUserName(), u.getMobile(),
                                  u.getDeptId(), u.getUnionId());
        }
    }
}
```

---

## 第五步：触发人员同步

Starter 自动注册了 `LtcUserSyncService` Bean，注入后调用 `sync()` 即可。

**推荐方式一：定时任务（每天自动同步）**

```java
import com.tranyu.ltc.inner.LtcUserSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserSyncJob {

    private final LtcUserSyncService ltcUserSyncService;

    public UserSyncJob(LtcUserSyncService ltcUserSyncService) {
        this.ltcUserSyncService = ltcUserSyncService;
    }

    // 每天早上 8 点自动同步
    @Scheduled(cron = "0 0 8 * * ?")
    public void sync() {
        ltcUserSyncService.sync();
    }
}
```

**推荐方式二：暴露管理接口（手动触发）**

```java
import com.tranyu.ltc.inner.LtcUserSyncService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final LtcUserSyncService ltcUserSyncService;

    public AdminController(LtcUserSyncService ltcUserSyncService) {
        this.ltcUserSyncService = ltcUserSyncService;
    }

    @PostMapping("/sync-users")
    public String syncUsers() {
        ltcUserSyncService.sync();
        return "同步完成";
    }
}
```

---

## 第六步：登录回调接口（AuthCallback）

项目里需要一个接收 LTC 扫码成功后回调的接口。注入 `LtcExchangeClient`：

```java
import com.tranyu.ltc.auth.LtcExchangeClient;
import com.tranyu.ltc.auth.LtcUserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthCallbackController {

    private final LtcExchangeClient ltcExchangeClient;

    public AuthCallbackController(LtcExchangeClient ltcExchangeClient) {
        this.ltcExchangeClient = ltcExchangeClient;
    }

    /**
     * LTC 扫码登录回调
     * LTC 会把 code 传到这个接口，用 code 换取用户身份，再签发自己的 JWT。
     */
    @GetMapping("/callback")
    public String callback(@RequestParam String code) {
        LtcUserContext user = ltcExchangeClient.exchange(code);
        Long userId = user.getTargetId();
        String name  = user.getDisplayName();

        // TODO：用 userId 签发 JWT，返回给前端
        return "登录成功，userId=" + userId + "，姓名=" + name;
    }
}
```

---

## 第七步：在 Controller 中使用当前用户

添加依赖和配置后，任意 Controller 方法都可以直接注入当前登录用户：

```java
import com.tranyu.ltc.auth.CurrentUser;
import com.tranyu.ltc.auth.LtcUserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @GetMapping("/my")
    public String myTasks(@CurrentUser LtcUserContext user) {
        Long userId = user.getTargetId();   // 当前用户 ID
        Long deptId = user.getDeptId();     // 部门 ID
        String name = user.getDisplayName(); // 姓名

        // 根据 userId 查询该用户的任务...
        return "当前用户：" + name + "（ID=" + userId + "）";
    }
}
```

如果请求未携带有效 LTC 登录态，框架会自动抛出 `LtcUnauthorizedException`，返回 401。

---

## 配置项完整说明

| 配置项 | 说明 | 默认值 |
|---|---|---|
| `ltc.auth.enabled` | 是否启用 LTC 鉴权 | `false` |
| `ltc.auth.exchange-url` | code 换用户信息的接口地址 | 无 |
| `ltc.auth.me-url` | vt 换用户信息的接口地址 | 无 |
| `ltc.auth.vt-header` | vt 请求头名 | `vt` |
| `ltc.auth.token-header` | token 请求头名 | `token` |
| `ltc.auth.allow-fallback-header` | 鉴权失败时是否放行（本地调试用） | `true` |
| `ltc.inner.url` | LTC 人员列表接口地址 | 无 |
| `ltc.inner.secret` | 内部接口鉴权 secret | 无 |

---

## 常见问题

**Q：本地开发不想每次都登录 LTC，怎么办？**

设置 `ltc.auth.enabled: false` 或 `allow-fallback-header: true`，
请求头加 `X-User-Id: 1`（你本地的用户 ID），框架会跳过 LTC 验证。

**Q：`@CurrentUser` 注入的用户为 null 怎么办？**

检查：① `ltc.auth.enabled` 是否为 true；② 请求是否携带了有效的 vt/token 头；③ `me-url` 是否配置正确。

**Q：人员同步时报 `ltc.inner.url 未配置`？**

确保 `application.yml` 里配置了 `ltc.inner.url` 和 `ltc.inner.secret`，
并且你的项目里有一个实现了 `LtcUserSyncHandler` 接口的 `@Component`。
