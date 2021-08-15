package org.oxerr.spring.cache.redis.scored.example.helloworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;

@SpringBootTest
class CachingApplicationTest {

	private final Logger log = LogManager.getLogger();
	private final CacheManager cacheManager;

	@Autowired
	public CachingApplicationTest(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Test
	void test() {
		log.info("Cache names: {}", () -> cacheManager.getCacheNames().stream().collect(Collectors.joining(", ")));

		RedisCache books = (RedisCache) cacheManager.getCache("books");
		RedisCache cache1 = (RedisCache) cacheManager.getCache("cache1");
		RedisCache cache2 = (RedisCache) cacheManager.getCache("cache2");

		assertEquals(Duration.ofMinutes(11), books.getCacheConfiguration().getTtl());
		assertEquals(Duration.ofMinutes(11), cache1.getCacheConfiguration().getTtl());
		assertEquals(Duration.ofMinutes(11), cache2.getCacheConfiguration().getTtl());

		assertTrue(books instanceof ScoredRedisCache);
		assertTrue(cache1 instanceof ScoredRedisCache);
		assertTrue(cache2 instanceof ScoredRedisCache);
	}

}
