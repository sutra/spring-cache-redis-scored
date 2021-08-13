package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.ChainedAnnotatedScoreResolver;
import org.springframework.lang.Nullable;

public class DefaultScoreResolver implements ScoreResolver {

	private final ChainedAnnotatedScoreResolver resolver;

	public DefaultScoreResolver() {
		this.resolver = new ChainedAnnotatedScoreResolver(
			"org.oxerr.spring.cache.redis.scored.score.resolver.annotation.Score",
			"javax.persistence.Version",
			"org.springframework.data.annotation.Version"
		);
	}

	@Override
	public Optional<Double> resolveScore(@Nullable Object value) {
		return this.resolver.resolveScore(value);
	}

}
