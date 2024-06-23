
## java_framework_summary（框架及工具整合）
[![](https://img.shields.io/badge/Language-Java-blue.svg)](https://github.com/1019509861/java_framework_summary)
[![](https://img.shields.io/badge/Framework-SpringBoot-brightgreen.svg)](https://github.com/1019509861/java_framework_summary)
[![](https://img.shields.io/badge/Database-MySql-blueviolet.svg)](https://github.com/1019509861/java_framework_summary)

### 后端框架
- [x] SpringBoot
- [x] SpringSecurity（JWT）
- [x] MyBatis
- [ ] Shiro
- [x] Netty（Netty 基础使用示例、WebSocket / UDP 示例）
- [x] ShardingSphere-JDBC
- [x] ElasticJob（底层依赖 Quartz）
- [ ] ~~XXL-Job（<span style="color:red">移除，不作引入</span>）~~
- [ ] 待补充...

### 数据库
- [x] MySql
- [x] Redis：未记录基础操作，仅包含 pub/sub、读写分离、集群配置（集群主要在于服务配置）
- [x] MongoDB：索引、文档 CRUD 及聚合操作，位于 test 包下
- [x] ElasticSearch

### 前端框架
- [x] Thymeleaf
- [x] JQuery
- [ ] 待补充...

### 中间件
- [x] RabbitMQ：RabbitMQ 只是小小调用，生产消费耦合在一个应用中，实际应当拆分为不同的服务.
- [x] Kafka：消息生产、消费、事务消息、TTL、Stream、自定义分区分配策略、拦截器、AdminClient 示例
- [x] RocketMQ：原生依赖生产及消费使用，集成 Spring boot starter 依赖
- [ ] 待补充...

### 工具
- [x] Logback
- [x] Lombok
- [x] Apache-commons
- [ ] Cache
- [ ] 待补充...
