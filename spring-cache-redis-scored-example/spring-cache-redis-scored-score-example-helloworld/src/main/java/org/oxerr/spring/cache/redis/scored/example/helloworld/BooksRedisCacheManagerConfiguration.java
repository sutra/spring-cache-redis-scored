package org.oxerr.spring.cache.redis.scored.example.helloworld;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/**
 * Check
 * <a href="https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#features.caching.provider.redis">
 * Spring Boot Documentation
 * </a>
 * for more about RedisCacheManagerBuilderCustomizer.
 */
@Configuration(proxyBeanMethods = false)
public class BooksRedisCacheManagerConfiguration {

	@Bean
	public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
		return builder -> builder
			.enableStatistics()
			.withCacheConfiguration(
				"books",
				RedisCacheConfiguration
					.defaultCacheConfig()
					.prefixCacheNameWith("spring-cache-redis-scored-example-helleworld::")
					.entryTtl(Duration.ofMinutes(10))
					.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
			);

	}

}
