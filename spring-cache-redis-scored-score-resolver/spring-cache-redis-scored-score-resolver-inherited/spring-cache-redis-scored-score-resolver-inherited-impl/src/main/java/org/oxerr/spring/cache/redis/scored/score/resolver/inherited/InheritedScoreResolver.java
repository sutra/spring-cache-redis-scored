package org.oxerr.spring.cache.redis.scored.score.resolver.inherited;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreUtils;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable.Versioned;

public class InheritedScoreResolver implements ScoreResolver {

	@Override
	public Optional<Double> resolveScore(Object value) {
		final Optional<Double> score;

		if (value instanceof Versioned) {
			Object version = ((Versioned<?>) value).getVersion();
			score = Optional.ofNullable(ScoreUtils.extractScore(version));
		} else {
			score = Optional.empty();
		}

		return score;
	}

}
