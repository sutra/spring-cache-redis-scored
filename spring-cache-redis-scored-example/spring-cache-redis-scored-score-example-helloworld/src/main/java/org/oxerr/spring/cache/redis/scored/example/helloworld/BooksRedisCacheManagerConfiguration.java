package org.oxerr.spring.cache.redis.scored.example.helloworld;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration(proxyBeanMethods = false)
public class BooksRedisCacheManagerConfiguration {

	@Bean
	public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer() {
		return builder -> builder
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
