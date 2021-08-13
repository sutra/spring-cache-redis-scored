## About spring-cache-redis-scored
Cache storage using Redis sorted set, to prevent stale data returned from cache querying.

### How to use

```java
@Cacheable(value = "books")
public Book getByIsbn(String isbn) {
}

@CachePut(value = "books", key = "#result.getIsbn()")
public Book save(Book book) {
}
```

### What is the different to RedisCache

The cache [implementation](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:support:cache-abstraction) `RedisCache`,
in [spring-data-redis](https://spring.io/projects/spring-data-redis) uses
[`set`](https://redis.io/commands/set)/[`setNX`](https://redis.io/commands/setnx),
[`get`](https://redis.io/commands/get)
commands to set/get cache entries
(see `org.springframework.data.redis.cache.RedisCacheWriter`),
this may cause stale data returned from caching querying, in this scenario:

1. the cached data is expired;
2. thread A queries data from persistence layer, got old data(version 1);
3. thread B writes new data(version 2) into persistence layer;
4. thread B **evicts old data** from cache;
5. thread A puts old data into cache;
6. now we have the old data(version 1) in cache,
and when querying from cache, the old data will be returned,
before the cache entry is expired or evicted.

But `ScoredRedisCache` uses
[`zAdd`](https://redis.io/commands/zadd),
[`zRevRangeByScore`](https://redis.io/commands/zrevrangebyscore)
and [`zRemRangeByScore`](https://redis.io/commands/zremrangebyscore),
commands to set/get cache entries
(see `org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter`),
this saves versioned data as sorted set in Redis with different scores,
and always returns the newest versioned data, lets replay the above scenario,
to demostrate how `ScoredRedisCache` prevent stale data:

1. the cached data is expired;
2. thread A queries data from persistence layer, got old data(version 1);
3. thread B writes new data(version 2) into persistence layer;
4. thread B **writes new data(version 2)** into cache with score 2;
5. thread A add old data(version 1) into cache with score 1;
6. now we have 2 verions of data in cache,
and when querying from cache, the newest version of data will be returned,
by using redis command `ZREVRANGEBYSCORE key +inf -inf limit 0 1`.
