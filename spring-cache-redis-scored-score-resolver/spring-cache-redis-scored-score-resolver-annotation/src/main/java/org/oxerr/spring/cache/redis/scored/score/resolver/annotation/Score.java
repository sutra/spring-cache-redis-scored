package org.oxerr.spring.cache.redis.scored.score.resolver.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Instant;
import java.util.Date;

/**
 * Specifies the score field or property.
 *
 * <p>
 * The following types and their sub types are supported for score properties:
 * </p>
 * <ul>
 * <li>{@link Number}</li>
 * <li>{@link Date}</li>
 * <li>{@link Instant}</li>
 * </ul>
 *
 * See {@code javax.persistence.Version},
 * {@code org.springframework.data.annotation.Version}.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Score {
}
