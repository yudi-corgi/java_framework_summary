package com.demo.allframework.mongodb;

import com.demo.allframework.mongodb.entity.User;
import com.demo.allframework.mongodb.entity.UserTemp;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateIndexOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Data;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author YUDI-Corgi
 * @description
 */
@SpringBootTest
public class MongoApplicationTest {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoApplicationTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Test
    public void collCreate() {

        // 判断集合是否存在
        if (!mongoTemplate.collectionExists("product")) {
            // 创建集合
            MongoCollection<Document> product = mongoTemplate.createCollection("product");
            System.out.println("创建集合 product：" + product.getNamespace());
        }

    }

    @Test
    public void collDelete() {

        mongoTemplate.dropCollection("product");
        System.out.println("删除集合 product");

    }

    @Test
    public void CollInfo() {

        // 获取指定集合信息
        MongoCollection<Document> product = mongoTemplate.getCollection("users");
        System.out.println(product);

        // 获取所有集合名称
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        collectionNames.forEach(System.out::println);

        // 通过 entity 获取集合名称，一般是使用 @Document 注解
        String collectionName = mongoTemplate.getCollectionName(User.class);
        System.out.println(collectionName);

    }

    @Test
    public void indexOps() {

        MongoCollection<Document> usersColl = mongoTemplate.getCollection("users");

        // 创建复合索引，索引属性唯一
        usersColl.createIndex(Indexes.ascending("age", "username"), new IndexOptions().unique(true));

        // 批量创建索引
        IndexModel index1 = new IndexModel(Indexes.ascending("money"), new IndexOptions().name("money_IDX"));
        IndexModel index2 = new IndexModel(Indexes.ascending("birth"), new IndexOptions().sparse(true).name("birth_IDX"));
        List<IndexModel> indexList = new ArrayList<>();
        Collections.addAll(indexList, index1, index2);
        usersColl.createIndexes(indexList, new CreateIndexOptions().maxTime(5, TimeUnit.SECONDS));

        // 删除索引
        usersColl.dropIndex("money_IDX");

        // 查询索引
        ListIndexesIterable<Document> documents = usersColl.listIndexes();
        documents.forEach(System.out::println);

    }

    @Test
    public void docSaveOrInsert() {

        // save：_id 存在则更新，无则插入
        User user = new User("111", "222", "333", "444", "214", "ppp", 20, new Date(), 3200.0);
        mongoTemplate.save(user);

        // insert：_id 存在则报 Key 重复异常，无则插入，支持批量插入
        mongoTemplate.insert(user);

        // 批量插入
        List<User> users = new ArrayList<>();
        users.add(new User("207", "jjj", 23, new Date()));
        users.add(new User("208", "kkk", 24, new Date()));
        users.add(new User("209", "lll", 25, new Date()));
        users.add(new User("210", "mmm", 25, new Date()));
        users.add(new User("211", "nnn", 26, new Date()));
        users.add(new User("212", "ooo", 30, new Date()));
        mongoTemplate.insert(users, "users");
        mongoTemplate.insertAll(users);

        // 关于实体所属集合的确定：
        // 默认使用 @Document 注解指定的集合名称，因为应用启动时便收集了类信息（包含注解信息），
        // 然后存储在 AbstractMappingContext#persistentEntities map 集合中
        // Map<TypeInformation<?>, Optional<E>>，Optional 泛型为 BasicMongoPersistentEntity

    }

    @Test
    public void docUpdate() {

        // 文档更新内容
        Update update = new Update();
        update.set("birth", new Date());

        // 查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("fff"));

        // 更新查询到的第一条文档
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
        System.out.println("查询匹配条数：" + updateResult.getMatchedCount());
        System.out.println("更新条数：" + updateResult.getModifiedCount());
        System.out.println("是否 ack：" + updateResult.wasAcknowledged());

        mongoTemplate.updateFirst(new Query(), update, "users");
        mongoTemplate.updateFirst(new Query(), update, User.class, "users");

        // 更新查询到的所有文档
        mongoTemplate.updateMulti(new Query(), update, User.class);
        mongoTemplate.updateMulti(new Query(), update, "users");
        mongoTemplate.updateMulti(new Query(), update, User.class, "users");

        // upsert：有则更新，无则插入
        Update upsert = new Update();
        upsert.set("birth", new Date());
        upsert.setOnInsert("_id", "206"); // 设置插入时 key 的值
        upsert.unset("name");             // 设置不更新的 key

        UpdateResult upsertResult = mongoTemplate.upsert(Query.query(Criteria.where("name").is("jjj")), upsert, User.class);
        System.out.println("upsertId（即生成的 _id）：" + upsertResult.getUpsertedId());

    }

    @Test
    public void docDelete() {

        // 删除所有
        mongoTemplate.remove(new Query(), User.class);

        // 条件删除
        DeleteResult removeResult = mongoTemplate.remove(Query.query(Criteria.where("name").is("fff")), User.class);
        System.out.println("删除条数：" + removeResult.getDeletedCount());
        System.out.println("是否 ack：" + removeResult.wasAcknowledged());

        mongoTemplate.remove(Query.query(Criteria.where("name").is("fff")), "users");
        mongoTemplate.remove(Query.query(Criteria.where("name").is("fff")), User.class, "users");

        // 传递实体，通过 _id 和 version 删除，使用 _id 字段删除要注意字段类型是否正确
        // version 字段要用注解 @Version 标识，用作乐观锁控制，若指定了 version 则会带上作为查询条件
        User user = new User();
        user.setId("203");
        // user.setVersion(1);
        mongoTemplate.remove(user);
        mongoTemplate.remove(user, "users");

        // 以链式 API 调用方式进行 remove 和 findAndRemove 操作，具体可看 remove 方法返参对象 ExecutableRemoveOperationSupport
        mongoTemplate.remove(User.class).matching(Query.query(Criteria.where("age").is(40))).all();

        // 当实体没有 @Document 注解指定集合名时，类名（首字母小写）便是集合名称
        // 可通过 inCollection() 来指定，而若实体已通过 @Document 指定集合名称则会覆盖
        mongoTemplate.remove(UserTemp.class).inCollection("users").matching(Query.query(Criteria.where("name").is("kts2"))).all();

        // 先查后删
        mongoTemplate.findAllAndRemove(Query.query(Criteria.where("name").is("eee")), User.class);
        mongoTemplate.findAllAndRemove(Query.query(Criteria.where("name").is("eee")), "users");
        mongoTemplate.findAllAndRemove(Query.query(Criteria.where("name").is("eee")), User.class, "users");

        // 先查后删，但只返回查询匹配的第一个文档
        mongoTemplate.findAndRemove(Query.query(Criteria.where("name").is("ccc")), User.class);
        mongoTemplate.findAndRemove(Query.query(Criteria.where("name").is("eee")), User.class, "users");

    }

    @Test
    public void docFind() {

        // PS：User 使用了 @Document 注解指定集合名称，UserTemp 中没有使用
        // PS：当实体没有使用 @Document 注解时，查询需要手动指定集合名称

        // region 查询所有

        mongoTemplate.findAll(User.class);
        mongoTemplate.findAll(UserTemp.class, "users");

        // 指定查询的字段
        Query query = new Query();
        query.fields().include("age", "name").exclude("_id");
        mongoTemplate.find(query, User.class);

        // endregion

        // region 根据唯一 ID 查询

        mongoTemplate.findById(200, User.class);

        // 这种方式，类中必须要有属性使用 @MongoId 标识为唯一 Id，查询才有结果
        // 因为在实际查询前，会先从类中查询唯一 ID 属性构建查询条件
        mongoTemplate.findById(201, UserTemp.class, "users");

        // endregion

        // region 条件查询

        // 空查询条件也等同于查询全部
        mongoTemplate.find(new Query(), User.class);
        mongoTemplate.find(new Query(), UserTemp.class, "users");

        // 比较运算符条件，查询时要注意数值的类型，不匹配则查询不到，不同于 MySQL 会自动转化
        mongoTemplate.find(Query.query(Criteria.where("age").is(29)), User.class);
        mongoTemplate.find(Query.query(Criteria.where("age").lt(32)), User.class);
        mongoTemplate.find(Query.query(Criteria.where("age").lte(32)), User.class);

        // in、not in
        mongoTemplate.find(Query.query(Criteria.where("age").in(20,21,22)), User.class);
        mongoTemplate.find(Query.query(Criteria.where("age").nin(20,21,22)), User.class);

        // and、or 查询
        mongoTemplate.find(Query.query(Criteria.where("age").gte(20).and("name").is("ccc")), User.class);
        mongoTemplate.find(Query.query(
                new Criteria().orOperator(
                    Criteria.where("name").is("aaa"),
                    Criteria.where("name").is("ccc"),
                    Criteria.where("age").lte(30)
                )), User.class);

        // and、or 共用，表示：age < 30 and (name = 'ccc' or name = 'eee')
        mongoTemplate.find(Query.query(
                Criteria.where("age").lt(30).orOperator(
                        Criteria.where("name").is("ccc"),
                        Criteria.where("name").is("eee")
                )), User.class);

        // 正则表达式（Java Pattern.compile）
        mongoTemplate.find(Query.query(Criteria.where("username").regex("^.*c.*$")), User.class);

        // endregion

        // region 排序、分页

        // 默认升序（asc），当排序条件都相同时，会通过 _id 使用指定方式排序
        mongoTemplate.find(new Query().with(Sort.by("age")), User.class);
        mongoTemplate.find(new Query().with(Sort.by("age", "name").descending()), User.class);
        mongoTemplate.find(new Query().with(Sort.by(Sort.Order.asc("age"))), User.class);
        mongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "age", "name")), User.class);

        // Sort.sort(User.class) 返回的是 TypedSort<User> 对象，通过方法句柄定义要排序的属性，因此可使用方法引用
        mongoTemplate.find(new Query().with(Sort.sort(User.class).by(User::getAge).ascending()), User.class);

        // 分页
        mongoTemplate.find(new Query().skip(2).limit(2), User.class);
        mongoTemplate.find(new Query().with(Pageable.ofSize(2)), User.class);
        // 表示不分页
        mongoTemplate.find(new Query().with(Pageable.unpaged()), User.class);
        // 排序后分页（不存在先分页后排序）
        mongoTemplate.find(new Query().limit(2), User.class);
        mongoTemplate.find(new Query().with(Sort.by("age").descending()).limit(2), User.class);
        mongoTemplate.find(new Query().with(Sort.by(Sort.Order.asc("age"))).with(Pageable.ofSize(2)), User.class);

        // endregion

        // region 聚合操作

        // region 单一目的聚合方法

        // 计数
        mongoTemplate.count(new Query(), User.class);
        mongoTemplate.count(new Query(), "users");
        mongoTemplate.count(Query.query(Criteria.where("age").lt(30)), User.class);

        // 去重
        // 以下两者效果相同，前者内部查询实现便是 new Query()
        mongoTemplate.findDistinct("age", User.class, Integer.class);
        mongoTemplate.findDistinct(new Query(), "age", User.class, Integer.class);

        // 以下两者效果相同，但前者多一个 User.class 是因为通过其获取 MongoPersistentEntity 信息，可以做字段映射（@Field）
        // 后者不传递默认是 Object.class，因此 MongoPersistentEntity 会为空
        mongoTemplate.findDistinct(new Query(), "age", "users", User.class, Integer.class);
        mongoTemplate.findDistinct(new Query(), "age", "users", Integer.class);

        // endregion

        // region 聚合管道

        // 过滤
        MatchOperation matchOps = Aggregation.match(Criteria.where("age").gte(25));
        // 分组：聚合操作的结果赋值给 as 指定的字段名
        GroupOperation groupOps = Aggregation.group("age")
                .count().as("userCount")
                .avg("money").as("avgMoney")
                .addToSet("name").as("usernames");
        // 排序
        SortOperation sortOps = Aggregation.sort(Sort.by("avgMoney").descending());
        // 限制数量
        LimitOperation limitOps = Aggregation.limit(6);
        // 添加字段
        AddFieldsOperation addFieldsOps = Aggregation.addFields().addFieldWithValue("createTime", new Date()).build();
        // 组合多个聚合操作形成聚合管道：先过滤、再分组、后排序、限数量、加字段
        TypedAggregation<User> aggregation = Aggregation.newAggregation(User.class, matchOps, groupOps, sortOps, limitOps, addFieldsOps);
        // 执行聚合操作，参数二：结果映射的实体类型，这里使用 Map，可以自定义 POJO 映射
        AggregationResults<AggregationEntity> aggregateResult = mongoTemplate.aggregate(aggregation, AggregationEntity.class);
        System.out.println("聚合结果：" + aggregateResult.getMappedResults());
        System.out.println("原始结果：" + aggregateResult.getRawResults());
        System.out.println("执行聚合操作的服务器：" + aggregateResult.getServerUsed());
        // System.out.println("返回唯一的结果（若聚合操作执行后有多个结果则异常）：" + aggregateResult.getUniqueMappedResult());

        // endregion

        // endregion

        // region JSON 语法查询

        // BasicQuery 子类可以使用任意 JSON 查询字符串设置查询条件
        Query basicQuery = new BasicQuery("{$or: [{name: 'ccc'}, {name: 'aaa'}]}}}");
        mongoTemplate.find(basicQuery, User.class);

        Query fuzzyQuery = new BasicQuery("{name: {$regex: /c.*g.*I$/, $options: 'si'}}");
        mongoTemplate.find(fuzzyQuery, User.class);

        // 参数二指定查询的字段
        Query basicQuery1 = new BasicQuery("{$or: [{name: 'ccc'}, {name: 'aaa'}]}}}", "{age: true, name: true, _id: 0}");
        mongoTemplate.find(basicQuery1, User.class);

        // 通过 Document 指定查询条件，其本质是一个 Map
        Document document = new Document();
        document.put("age", new Document("$gt", 22).append("$lt", 30));
        Query basicQuery2 = new BasicQuery(document);
        mongoTemplate.find(basicQuery2, User.class);

        // 通过 Document 对象指定查询的字段
        Document document1 = new Document();
        document1.put("name", true);
        Query basicQuery3 = new BasicQuery(document, document1);
        mongoTemplate.find(basicQuery3, User.class);

        // endregion
    }

}

@Data
class AggregationEntity {
    private String id;
    private Integer userCount;
    private Double avgMoney;
    private String[] usernames;
    private Date createTime;
}