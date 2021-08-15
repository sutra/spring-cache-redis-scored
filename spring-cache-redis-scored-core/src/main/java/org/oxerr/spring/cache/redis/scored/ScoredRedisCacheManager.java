package org.oxerr.spring.cache.redis.scored;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ScoredRedisCacheManager extends RedisCacheManager {

	private final ScoredRedisCacheWriter cacheWriter;
	private final ScoredRedisCacheConfiguration defaultCacheConfig;
	private final Map<String, ScoredRedisCacheConfiguration> initialCacheConfiguration;

	private ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		boolean allowInFlightCacheCreation
	) {
		super(
			cacheWriter,
			defaultCacheConfiguration.getRedisCacheConfiguration(),
			allowInFlightCacheCreation
		);

		this.cacheWriter = cacheWriter;
		this.defaultCacheConfig = defaultCacheConfiguration;
		this.initialCacheConfiguration = new LinkedHashMap<>();
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration
	) {
		this(cacheWriter, defaultCacheConfiguration, true);
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		String... initialCacheNames
	) {
		this(cacheWriter, defaultCacheConfiguration, true, initialCacheNames);
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		boolean allowInFlightCacheCreation,
		String... initialCacheNames
	) {
		this(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation);

		for (String cacheName : initialCacheNames) {
			this.initialCacheConfiguration.put(cacheName, defaultCacheConfiguration);
		}
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		Map<String, ScoredRedisCacheConfiguration> initialCacheConfigurations
	) {
		this(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, true);
	}

	public ScoredRedisCacheManager(
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration defaultCacheConfiguration,
		Map<String, ScoredRedisCacheConfiguration> initialCacheConfigurations,
		boolean allowInFlightCacheCreation
	) {
		this(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation);

		Assert.notNull(initialCacheConfigurations, "InitialCacheConfigurations must not be null!");

		this.initialCacheConfiguration.putAll(initialCacheConfigurations);
	}


	@Override
	protected Collection<RedisCache> loadCaches() {

		List<RedisCache> caches = new LinkedList<>();

		for (Map.Entry<String, ScoredRedisCacheConfiguration> entry : initialCacheConfiguration.entrySet()) {
			caches.add(createRedisCache(entry.getKey(), entry.getValue()));
		}

		return caches;
	}

	@Override
	protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
		ScoredRedisCacheConfiguration scoredRedisCacheConfiguration = cacheConfig != null
			? new ScoredRedisCacheConfiguration(cacheConfig, defaultCacheConfig.getScoreResolver())
			: defaultCacheConfig;
		return createRedisCache(name, scoredRedisCacheConfiguration);
	}

	protected RedisCache createRedisCache(
		String name,
		@Nullable ScoredRedisCacheConfiguration cacheConfig
	) {
		return new ScoredRedisCache(
			name,
			cacheWriter,
			cacheConfig != null ? cacheConfig : defaultCacheConfig
		);
	}

}
