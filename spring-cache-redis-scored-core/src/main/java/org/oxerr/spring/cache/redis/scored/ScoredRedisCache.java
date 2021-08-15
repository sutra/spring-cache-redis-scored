package org.oxerr.spring.cache.redis.scored;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.lang.Nullable;

public class ScoredRedisCache extends RedisCache {

	private final ScoreResolver scoreResolver;

	protected ScoredRedisCache(
		String name,
		ScoredRedisCacheWriter cacheWriter,
		ScoredRedisCacheConfiguration cacheConfig
	) {
		super(name, cacheWriter, cacheConfig.getRedisCacheConfiguration());

		this.scoreResolver = cacheConfig.getScoreResolver();
	}

	@Override
	protected Object preProcessCacheValue(Object value) {
		this.setScore(value);
		return super.preProcessCacheValue(value);
	}

	private void setScore(@Nullable Object value) {
		Double score = this.scoreResolver.resolveScore(value).orElse(null);
		ScoreHolder.set(score);
	}

}
