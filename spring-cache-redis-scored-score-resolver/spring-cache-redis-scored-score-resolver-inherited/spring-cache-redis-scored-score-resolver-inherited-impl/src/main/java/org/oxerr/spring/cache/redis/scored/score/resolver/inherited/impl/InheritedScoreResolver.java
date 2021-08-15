package org.oxerr.spring.cache.redis.scored.score.resolver.inherited.impl;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable.Scored;

public class InheritedScoreResolver implements ScoreResolver {

	@Override
	public Optional<Double> resolveScore(Object value) {
		final Double score;

		if (value instanceof Scored) {
			score = ((Scored) value).getScore();
		} else {
			score = null;
		}

		return Optional.ofNullable(score);
	}

}
