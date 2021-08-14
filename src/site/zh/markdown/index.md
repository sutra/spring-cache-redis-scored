## 关于 Spring Cache Redis Scored
使用 Redis 有序集合存储的 Spring 缓存实现，可避免缓存查询时返回脏数据。

### 如何使用

#### 添加依赖

```xml
<dependency>
	<groupId>org.oxerr.spring.cache.redis.scored</groupId>
	<artifactId>spring-cache-redis-scored-spring-boot-starter</artifactId>
</dependency>
```

#### 使用注解标记缓存数据类里的版本字段或方法，以便 ScoreResolver 从中获取值

```java
import org.oxerr.spring.cache.redis.scored.score.resolver.annotation.Score;
// import javax.persistence.Version;
// import org.springframework.data.annotation.Version;

public class Book {

	/**
	 * 查阅 org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure.DefaultScoreResolver
	 * 来获知支持的注解。
	 */
	@Score
	// @Version
	private long version;

}
```

#### Use Spring cache annotations to cache data
```java
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Cacheable(value = "books")
public Book getByIsbn(String isbn) {
	Book book = getByIsbnFromPersistence(isbn);

	// 一些比较耗时的操作

	return book;
}

/**
 * 在这里使用 @CachePut 来确保，写完持久层后，将新版本写入缓存。
 */
@CachePut(value = "books", key = "#result.getIsbn()")
public Book save(Book book) {

	// 可以使用数据库行记录锁或者乐观锁，来确保 book.version 字段在并发请求的情况下安全地递增。

	return saveToPersistence(isbn);
}
```

### 和 RedisCache 的区别

[spring-data-redis](https://spring.io/projects/spring-data-redis)里的缓存
[实现](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:support:cache-abstraction) `RedisCache`，
使用
[`set`](https://redis.io/commands/set)/[`setNX`](https://redis.io/commands/setnx),
[`get`](https://redis.io/commands/get)
命令来存取缓存数据
（见 [`org.springframework.data.redis.cache.RedisCacheWriter`](https://github.com/spring-projects/spring-data-redis/blob/main/src/main/java/org/springframework/data/redis/cache/DefaultRedisCacheWriter.java)），
这在下面的情况下，可能导致在查询缓存时返回脏数据
（参见项目 `spring-cache-redis-scored-example-spring-data-redis-cache`）。

假设有两个并发请求，一个请求 A 做查询操作，一个请求 B 做更新操作，那么会有如下情形发生：

1. 缓存刚好失效。
2. 请求 A 查询持久层，得到一个旧值（版本 1）。
3. 请求 B 将新值（版本 2）写入持久层。
4. **请求 B 删除缓存。**
5. 请求 A 将查到的旧值（版本 1）写入缓存。

那么现在，在缓存过期或者主动清除前，查询缓存，就会返回旧值（版本 1）。

`ScoredRedisCache` 则使用
[`zAdd`](https://redis.io/commands/zadd),
[`zRevRangeByScore`](https://redis.io/commands/zrevrangebyscore)
和 [`zRemRangeByScore`](https://redis.io/commands/zremrangebyscore),
命令来存取缓存
（见 `org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter`），
这将不同版本的数据存储为 Redis 里有序集合的不同 score 的元素，
当查询缓存时，总是返回 score 最大的元素，
我们再来演示一下上述场景，看看`ScoredRedisCache`是如何避免返回脏数据的
(参见项目 `spring-cache-redis-scored-example-spring-cache-redis-scored`)：

1. 缓存刚好失效。
2. 请求 A 查询持久层，得到一个旧值（版本 1）。
3. 请求 B 将新值（版本 2）写入持久层。
4. **请求 B 将新值写入缓存（版本 2），score 为 2。**
5. 请求 A 将查询到的旧值（版本 1）添加到缓存，score 为 1。

现在我们在缓存中有两个版本的数据，
版本 1 存储为 score = 1，版本 2 存储为 score = 2。
当查询缓存时，则通过使用 Redis 命令 `ZREVRANGEBYSCORE key +inf -inf limit 0 1`
返回 score 为最大值的最新版本（score = 2，version = 2）。
