package org.oxerr.spring.cache.redis.scored.score.resolver;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.ChainedAnnotatedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.chained.ChainedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.InheritedScoreResolver;
import org.springframework.lang.Nullable;

public class DefaultScoreResolver implements ScoreResolver {

	private final ScoreResolver resolver;

	public DefaultScoreResolver() {
		final ChainedAnnotatedScoreResolver chainedAnnotatedScoreResolver = new ChainedAnnotatedScoreResolver(
			"org.oxerr.spring.cache.redis.scored.score.resolver.annotated.annotation.Score",
			"javax.persistence.Version",
			"org.springframework.data.annotation.Version"
		);
		this.resolver = new ChainedScoreResolver(
			new InheritedScoreResolver(),
			chainedAnnotatedScoreResolver
		);
	}

	@Override
	public Optional<Double> resolveScore(@Nullable Object value) {
		return this.resolver.resolveScore(value);
	}

}
