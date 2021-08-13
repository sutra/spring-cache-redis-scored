package org.oxerr.spring.cache.redis.scored;

import java.util.Map;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

public class ScoredRedisCacheManager extends RedisCacheManager {

	private final ScoredRedisCacheWriter cacheWriter;
	private final ScoredRedisCacheConfiguration defaultCacheConfig;

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		boolean allowInFlightCacheCreation,
		String... initialCacheNames
	) {
		super(cacheWriter, defaultCacheConfiguration.getRedisCacheConfiguration(), allowInFlightCacheCreation, initialCacheNames);

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		Map<String, RedisCacheConfiguration> initialCacheConfigurations,
		boolean allowInFlightCacheCreation
	) {
		super(cacheWriter, defaultCacheConfiguration.getRedisCacheConfiguration(), initialCacheConfigurations, allowInFlightCacheCreation);

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		Map<String, RedisCacheConfiguration> initialCacheConfigurations
	) {
		super(cacheWriter, defaultCacheConfiguration.getRedisCacheConfiguration(), initialCacheConfigurations);

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		String... initialCacheNames
	) {
		super(cacheWriter, defaultCacheConfiguration.getRedisCacheConfiguration(), initialCacheNames);

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration
	) {
		super(cacheWriter, defaultCacheConfiguration.getRedisCacheConfiguration());

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
	}

	@Override
	protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
		ScoredRedisCacheConfiguration scoredRedisCacheConfiguration = cacheConfig != null
			? new ScoredRedisCacheConfiguration(cacheConfig, defaultCacheConfig.getScoreResolver())
			: defaultCacheConfig;
		return new ScoredRedisCache(name, cacheWriter, scoredRedisCacheConfiguration);
	}

}
