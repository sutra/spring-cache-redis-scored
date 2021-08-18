package org.oxerr.spring.cache.redis.scored.score.resolver.inherited;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable.Scored;

public class InheritedScoreResolver implements ScoreResolver {

	@Override
	public Optional<Double> resolveScore(Object value) {
		final Optional<Double> score;

		if (value instanceof Scored) {
			score = Optional.ofNullable(((Scored) value).getScore());
		} else {
			score = Optional.empty();
		}

		return score;
	}

}
