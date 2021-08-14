## About Spring Cache Redis Scored
Cache storage using Redis sorted set, to prevent returning stale data.

### How to use

#### Add dependencies to your project

```xml
<dependency>
	<groupId>org.oxerr.spring.cache.redis.scored</groupId>
	<artifactId>spring-cache-redis-scored-spring-boot-starter</artifactId>
</dependency>
```

#### Mark your cached data class with supported annotation for ScoreResolver

```java
import org.oxerr.spring.cache.redis.scored.score.resolver.annotation.Score;
// import javax.persistence.Version;
// import org.springframework.data.annotation.Version;

public class Book {

	/**
	 * Check org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure.DefaultScoreResolver
	 * to see the supported annotations.
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

	// Maybe some slow works go here.

	return book;
}

/**
 * The @CachePut is required for preventing stale data.
 */
@CachePut(value = "books", key = "#result.getIsbn()")
public Book save(Book book) {

	// Make sure the book.version is incremental and incrementing thread-safely,
	// by using locking mechanism such as database record locking or optimistic locking.

	return saveToPersistence(isbn);
}
```

### What is the different to RedisCache

The cache [implementation](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:support:cache-abstraction) `RedisCache`,
in [spring-data-redis](https://spring.io/projects/spring-data-redis) uses
[`set`](https://redis.io/commands/set)/[`setNX`](https://redis.io/commands/setnx),
[`get`](https://redis.io/commands/get)
commands to set/get cache entries
(see [`org.springframework.data.redis.cache.RedisCacheWriter`](https://github.com/spring-projects/spring-data-redis/blob/main/src/main/java/org/springframework/data/redis/cache/DefaultRedisCacheWriter.java)),
this may cause stale data returned from caching querying, in this scenario
(see project `spring-cache-redis-scored-example-spring-data-redis-cache`).

Assuming we have 2 concurrent requests,
request A querying data, and request B updating data,
then the following scenario may happen:

1. The cached data is expired.
2. Request A queries data from persistence layer, got old data(version 1).
3. Request B writes new data(version 2) into persistence layer.
4. **Request B evicts old data from cache.**
5. Request A puts old data(version 1) into cache.

Now we have the old data(version 1) in cache,
and when querying from cache, the old data(version 1) will be returned,
before the cache entry is expired or evicted.

But `ScoredRedisCache` uses
[`zAdd`](https://redis.io/commands/zadd),
[`zRevRangeByScore`](https://redis.io/commands/zrevrangebyscore)
and [`zRemRangeByScore`](https://redis.io/commands/zremrangebyscore),
commands to set/get cache entries
(see `org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter`),
this saves versioned data as sorted set in Redis with different scores,
and always returns the newest versioned data, lets replay the above scenario,
to demonstrate how `ScoredRedisCache` prevents stale data
(see project `spring-cache-redis-scored-example-spring-cache-redis-scored`):

1. The cached data is expired.
2. Request A queries data from persistence layer, got old data(version 1).
3. Request B writes new data(version 2) into persistence layer.
4. **Request B writes new data(version 2) into cache with score 2.**
5. Request A adds old data(version 1) into cache with score 1.

Now we have 2 versions of data in cache,
the version 1 is score = 1, and the version 2 is score = 2.
And when querying from cache,
the newest version with maximum score(score = 2, version = 2) of data
will be returned,
by using Redis command `ZREVRANGEBYSCORE key +inf -inf limit 0 1`.
