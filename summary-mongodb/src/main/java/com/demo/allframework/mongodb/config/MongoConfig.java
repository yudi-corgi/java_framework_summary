package com.demo.allframework.mongodb.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.SslSettings;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * @author YUDI-Corgi
 * @description Mongo 配置，该类并未使用，配置放在 application.yml
 */
@Configuration
public class MongoConfig {

    /**
     * MongoDB 原生客户端创建
     * @return MongoClient
     */
    public MongoClient mongoClient() {

        // 方式一：若有账密，则为 mongodb://user:pass@localhost:27017
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        // 方式二：
        ConnectionString cs = new ConnectionString("mongodb://localhost:27017");
        MongoClient mongoClient1 = MongoClients.create(cs);

        // 方式三：指定连接地址、账密和库、禁用 SSL
        MongoClientSettings.Builder setBuilder = MongoClientSettings.builder();
        setBuilder.applyConnectionString(cs);
        MongoCredential credential = MongoCredential.createCredential("username", "database", "pass".toCharArray());
        setBuilder.credential(credential);
        Block<SslSettings.Builder> block = (builder) -> builder.enabled(false);
        setBuilder.applyToSslSettings(block);
        MongoClient mongoClient2 = MongoClients.create(setBuilder.build());

        // 方式四：三个驱动参数是指针对本次连接要添加的额外信息，这些额外信息会在日志中被打印
        MongoDriverInformation.Builder infoBuilder = MongoDriverInformation.builder();
        infoBuilder.driverName("Demo");
        infoBuilder.driverPlatform("windows");
        infoBuilder.driverVersion("Java 8");
        MongoClient mongoClient3 = MongoClients.create(cs, infoBuilder.build());

        // 方式五：
        MongoClient mongoClient4 = MongoClients.create(setBuilder.build(), infoBuilder.build());

        return mongoClient;
    }

    /**
     * MongoTemplate 创建
     * @return MongoTemplate
     */
    public MongoTemplate mongoTemplate() {

        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        // 方式一：可通过 MongoClients 设置属性，如上方 MongoClients 创建方式三
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "database");

        // 方式二：Factory 对象同样可通过 MongoClient 构造
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient, "database");
        MongoTemplate mongoTemplate1 = new MongoTemplate(factory);

        // 方式三：MongoConverter 是 BSON 与 Object 之间的转换器，传递 null 会使用默认的转换器
        MongoTemplate mongoTemplate2 = new MongoTemplate(factory, null);

        return mongoTemplate;
    }

}
