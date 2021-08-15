package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.ChainedAnnotatedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.chained.ChainedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.InheritedScoreResolver;
import org.springframework.lang.Nullable;

public class DefaultScoreResolver implements ScoreResolver {

	private final ScoreResolver resolver;

	public DefaultScoreResolver() {
		ChainedAnnotatedScoreResolver chainedAnnotatedScoreResolver = new ChainedAnnotatedScoreResolver(
			"org.oxerr.spring.cache.redis.scored.score.resolver.annotated.annotation.Score",
			"javax.persistence.Version",
			"org.springframework.data.annotation.Version"
		);
		this.resolver = new ChainedScoreResolver(new InheritedScoreResolver(), chainedAnnotatedScoreResolver);
	}

	@Override
	public Optional<Double> resolveScore(@Nullable Object value) {
		return this.resolver.resolveScore(value);
	}

}
