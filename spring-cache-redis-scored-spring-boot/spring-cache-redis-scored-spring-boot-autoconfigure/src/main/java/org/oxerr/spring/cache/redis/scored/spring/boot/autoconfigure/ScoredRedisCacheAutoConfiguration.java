package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.oxerr.spring.cache.redis.scored.InheritableThreadLocalScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter;
import org.oxerr.spring.cache.redis.scored.score.resolver.DefaultScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.serializer.ScoredRedisSerializer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * See {@code org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}.
 *
 * <a href="https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#features.caching.provider.redis">
 * Spring Boot Features - Caching - Providers - Redis
 * </a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ScoredRedisSerializer.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
class ScoredRedisCacheAutoConfiguration {

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
		@NonNull RedisConnectionFactory connectionFactory,
		@Nullable ClassLoader classLoader
	) {
		return builder -> {
			final ScoreResolver scoreResolver = new DefaultScoreResolver();
			final ScoreHolder scoreHolder = new InheritableThreadLocalScoreHolder();

			final RedisSerializer<Object> serializer = RedisSerializer.java(classLoader);
			final ScoredRedisSerializer scoredRedisSerializer = new ScoredRedisSerializer(serializer, scoreResolver, scoreHolder);
			final SerializationPair<?> valueSerializationPair = SerializationPair.fromSerializer(scoredRedisSerializer);

			final RedisCacheConfiguration cacheDefaults = getDefaultCacheConfiguration(builder).serializeValuesWith(valueSerializationPair);
			builder.cacheDefaults(cacheDefaults);

			final ScoredRedisCacheWriter cacheWriter = new ScoredRedisCacheWriter(
				connectionFactory,
				CacheStatisticsCollector.none(),
				scoreHolder,
				this.getRedisCacheWriter(builder)
			);
			builder.cacheWriter(cacheWriter);
		};
	}

	private RedisCacheConfiguration getDefaultCacheConfiguration(RedisCacheManagerBuilder builder) {
		return this.readDeclaredField(builder, "defaultCacheConfiguration");
	}

	private RedisCacheWriter getRedisCacheWriter(RedisCacheManagerBuilder builder) {
		return this.readDeclaredField(builder, "cacheWriter");
	}

	@SuppressWarnings("unchecked")
	private <T> T readDeclaredField(RedisCacheManagerBuilder builder, String fieldName) {
		try {
			return (T) FieldUtils.readDeclaredField(builder, fieldName, true);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
