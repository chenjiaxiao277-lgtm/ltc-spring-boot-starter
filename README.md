# ltc-spring-boot-starter

LTC 登录鉴权 + 人员同步 Spring Boot Starter。

## 安装

```bash
mvn clean install
```

然后在接入方项目 pom.xml 加依赖：

```xml
<dependency>
    <groupId>com.tranyu</groupId>
    <artifactId>ltc-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 配置

```yaml
ltc:
  auth:
    enabled: true                                                          # 开启鉴权
    exchange-url: https://ltc.tranyu.com/ltc/v1.0/auth/exchange-code      # code 换用户信息
    me-url: https://ltc.tranyu.com/ltc/v1.0/customer/me                   # vt 换用户信息
    allow-fallback-header: false                                           # 生产环境关闭
  inner:
    url: https://ltc.tranyu.com/ltc/v1.0/inner/user/list                  # 人员列表接口
    secret: your-inner-secret                                              # 内部接口鉴权
```

## 使用

### 1. Controller 获取当前用户

```java
@GetMapping("/example")
public Result example(@CurrentUser LtcUserContext user) {
    Long userId = user.getTargetId();   // LTC targetId
    Long deptId = user.getDeptId();
    String name = user.getDisplayName();
    // ...
}
```

### 2. 人员同步

实现 `LtcUserSyncHandler` 接口，框架自动注入 `LtcUserSyncService`，调用 `sync()` 触发同步：

```java
@Component
public class MyUserSyncHandler implements LtcUserSyncHandler {
    @Autowired
    private MyUserRepository repo;

    @Override
    public void onUserList(List<LtcInnerUser> users) {
        for (LtcInnerUser u : users) {
            // upsert 到自己的用户表
            // u.getTargetId()  — 用户 ID
            // u.getUserName()  — 姓名
            // u.getMobile()    — 手机号
            // u.getDeptId()    — 部门 ID
            // u.getUnionId()   — 飞书 union_id
        }
    }
}

// 定时或手动触发同步
@Autowired
private LtcUserSyncService ltcUserSyncService;

public void triggerSync() {
    ltcUserSyncService.sync();
}
```

### 3. AuthCallback（LTC 扫码登录回调）

注入 `LtcExchangeClient`，用 code 换用户信息：

```java
@Autowired
private LtcExchangeClient ltcExchangeClient;

public void callback(String code) {
    LtcUserContext user = ltcExchangeClient.exchange(code);
    // user.getTargetId() 即为登录用户 ID
}
```
