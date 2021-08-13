package org.oxerr.spring.cache.redis.scored;

import org.springframework.data.redis.cache.RedisCacheConfiguration;

public class ScoredRedisCacheConfiguration {

	private final RedisCacheConfiguration redisCacheConfiguration;

	private final ScoreResolver scoreResolver;

	public ScoredRedisCacheConfiguration(
		RedisCacheConfiguration redisCacheConfiguration,
		ScoreResolver scoreResolver
	) {
		this.redisCacheConfiguration = redisCacheConfiguration;
		this.scoreResolver = scoreResolver;
	}

	public RedisCacheConfiguration getRedisCacheConfiguration() {
		return redisCacheConfiguration;
	}

	public ScoreResolver getScoreResolver() {
		return scoreResolver;
	}

}
