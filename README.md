# app-ais-export-vehicle

智能服务系统--出口--整车订单业务服务（CBU/SKD/CKD）。

干净的 DDD 框架（Spring Boot 3.5 + Maven 多模块），内置 `DemoOrder` 全分层贯穿示例。

## 本地启动

构建需 JDK 17（系统默认 mvn 若为 JDK 8 会报 `无效的目标发行版: 17`，请将 `JAVA_HOME` 指向本机 JDK 17）：

```bash
JAVA_HOME="<本机JDK17路径>" PATH="<本机JDK17路径>/bin:$PATH" mvn clean package -DskipTests
cd app-ais-export-vehicle-start && mvn spring-boot:run
```

启动前需配置以下环境变量覆盖 Nacos 连接，并将 `namespace` 改为**个人 namespace**（勿用代码默认的公共 namespace）：

```
spring.cloud.nacos.discovery.server-addr=https://leapx-nacos-test.leapmotor.com
spring.cloud.nacos.discovery.namespace=<个人namespace>
spring.cloud.nacos.discovery.username=nAdmin
spring.cloud.nacos.discovery.password=<密码>
```

## 参考文档（内网 Wiki）

- 框架介绍：https://wiki-intra.leapmotor.com/pages/viewpage.action?pageId=90433541
- 本地启动 Nacos 个人 namespace 配置：https://wiki-intra.leapmotor.com/pages/viewpage.action?pageId=118531368
