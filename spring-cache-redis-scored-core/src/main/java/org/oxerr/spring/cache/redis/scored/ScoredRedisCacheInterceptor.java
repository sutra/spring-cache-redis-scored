package org.oxerr.spring.cache.redis.scored;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ScoredRedisCacheInterceptor extends CacheInterceptor {

	private static final long serialVersionUID = 2021082001L;

	private final ScoreResolver scoreResolver;
	private final ScoreHolder scoreHolder;

	public ScoredRedisCacheInterceptor(
		@NonNull ScoreResolver scoreResolver,
		@NonNull ScoreHolder scoreHolder
	) {
		this.scoreResolver = scoreResolver;
		this.scoreHolder = scoreHolder;
	}

	@Override
	protected void doPut(Cache cache, Object key, @Nullable Object result) {
		try {
			this.setScore(result);
			super.doPut(cache, key, result);
		} finally {
			this.clearScore();
		}
	}

	private void setScore(@Nullable Object result) {
		Optional<Double> score = this.scoreResolver.resolveScore(result);
		this.scoreHolder.set(score.orElse(null));
	}

	private void clearScore() {
		this.scoreHolder.remove();
	}

}
