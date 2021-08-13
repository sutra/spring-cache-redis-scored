package org.oxerr.spring.cache.redis.scored;

import java.util.Optional;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ScoreResolver {

	/**
	 * Resolve score from the value.
	 *
	 * @param value the value.
	 * @return the score.
	 */
	Optional<Double> resolveScore(@Nullable Object value);

}
