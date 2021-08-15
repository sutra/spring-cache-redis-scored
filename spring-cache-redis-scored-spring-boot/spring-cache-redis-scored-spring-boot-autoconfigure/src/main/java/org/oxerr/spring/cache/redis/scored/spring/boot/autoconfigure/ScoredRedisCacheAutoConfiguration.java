package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import java.util.stream.Collectors;

import org.oxerr.spring.cache.redis.scored.ScoredRedisCache;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheConfiguration;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheManager;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@ConditionalOnClass({ ScoredRedisCache.class, RedisConnectionFactory.class })
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties(CacheProperties.class)
class ScoredRedisCacheAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
		return new CacheManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
	}

	@Bean
	ScoredRedisCacheManager scoredRedisCacheManager(
		CacheProperties cacheProperties,
		CacheManagerCustomizers cacheManagerCustomizers,
		ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
		RedisConnectionFactory redisConnectionFactory,
		ResourceLoader resourceLoader
	) {
		final CacheStatisticsCollector cacheStatisticsCollector = cacheProperties.getRedis().isEnableStatistics() ? CacheStatisticsCollector.create() : CacheStatisticsCollector.none();
		final ScoredRedisCacheWriter cacheWriter = new ScoredRedisCacheWriter(redisConnectionFactory, cacheStatisticsCollector);

		final ScoredRedisCacheConfiguration defaultCacheConfiguration = new ScoredRedisCacheConfiguration(
			determineConfiguration(cacheProperties, redisCacheConfiguration, resourceLoader.getClassLoader()),
			new DefaultScoreResolver()
		);
		final boolean allowInFlightCacheCreation = true;

		final ScoredRedisCacheManager cacheManager =  new ScoredRedisCacheManager(
			cacheWriter,
			defaultCacheConfiguration,
			allowInFlightCacheCreation,
			cacheProperties.getCacheNames().stream().toArray(String[]::new)
		);

		return cacheManagerCustomizers.customize(cacheManager);
	}

	private org.springframework.data.redis.cache.RedisCacheConfiguration determineConfiguration(
			CacheProperties cacheProperties,
			ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration,
			ClassLoader classLoader) {
		return redisCacheConfiguration.getIfAvailable(() -> createConfiguration(cacheProperties, classLoader));
	}

	private org.springframework.data.redis.cache.RedisCacheConfiguration createConfiguration(
			CacheProperties cacheProperties, ClassLoader classLoader) {
		Redis redisProperties = cacheProperties.getRedis();
		org.springframework.data.redis.cache.RedisCacheConfiguration config = org.springframework.data.redis.cache.RedisCacheConfiguration
				.defaultCacheConfig();
		config = config.serializeValuesWith(
				SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		return config;
	}

}
