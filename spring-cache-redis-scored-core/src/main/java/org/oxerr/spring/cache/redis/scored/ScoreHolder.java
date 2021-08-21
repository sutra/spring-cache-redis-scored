package org.oxerr.spring.cache.redis.scored;

import java.io.Serializable;

/**
 * Score holder.
 */
public interface ScoreHolder extends Serializable {

	/**
	 * Returns the score.
	 *
	 * @return the score, may be {@literal null}.
	 */
	Double get();

	/**
	 * Set the score.
	 *
	 * @param score the score, may be {@literal null}.
	 */
	void set(Double score);

	/**
	 * Remove the score.
	 */
	void remove();

}
