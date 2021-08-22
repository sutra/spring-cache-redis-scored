package org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @param <T> the version value type.
 * <p>
 * The following types and their sub types are supported:
 * </p>
 * <ul>
 * <li>{@link Number}</li>
 * <li>{@link Timestamp}</li>
 * <li>{@link Instant}</li>
 * </ul>
 */
public interface Versioned<T> {

	/**
	 * Returns the version.
	 *
	 * @return the version.
	 */
	T getVersion();

}
