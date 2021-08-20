package org.oxerr.spring.cache.redis.scored.score.resolver;

import java.io.Serializable;
import java.util.Optional;

@FunctionalInterface
public interface ScoreResolver extends Serializable {

	/**
	 * Resolve score from the value.
	 *
	 * @param value the value, may be null.
	 * @return the score.
	 */
	Optional<Double> resolveScore(Object value);

}
