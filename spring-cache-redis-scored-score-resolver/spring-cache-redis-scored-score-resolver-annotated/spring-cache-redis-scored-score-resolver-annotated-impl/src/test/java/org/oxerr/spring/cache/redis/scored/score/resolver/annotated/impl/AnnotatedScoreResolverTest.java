package org.oxerr.spring.cache.redis.scored.score.resolver.annotated.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.AnnotatedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.annotation.Score;

class AnnotatedScoreResolverTest {

	private final AnnotatedScoreResolver scoreResolver = new AnnotatedScoreResolver(Score.class);

	@Test
	void testResolveScoreNull() {
		assertEquals(Optional.empty(), scoreResolver.resolveScore(null));
	}

	@Test
	void testResolveScoreFromField() {
		assertEquals(1, scoreResolver.resolveScore(new AccountFieldVersion()).get().longValue());
	}

	@Test
	void testResolveScoreFromMethod() {
		assertEquals(2, scoreResolver.resolveScore(new AccountMethodVersion()).get().longValue());
		assertEquals(2, scoreResolver.resolveScore(new AccountFieldMethodVersion()).get().longValue());
	}

	@Test
	void testResolveScoreTimestamp() {
		AccountTimestamp a = new AccountTimestamp();
		assertEquals(
			Double.parseDouble(String.format("%d.%d", a.version.getTime(), a.version.getNanos())),
			this.scoreResolver.resolveScore(a).get()
		);
	}

	@Test
	void testResolveScoreInstant() {
		AccountInstant a = new AccountInstant();
		assertEquals(
			Double.parseDouble(String.format("%d.%d", a.version.toEpochMilli(), a.version.getNano())),
			this.scoreResolver.resolveScore(a).get()
		);
	}

	@Test
	void testResolveScoreUnsupportedType() {
		AccountUnsupportedType a = new AccountUnsupportedType();
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.scoreResolver.resolveScore(a));
	}

}

class AccountFieldVersion {

	@Score
	private long version = 1L;

}

class AccountMethodVersion {

	@Score
	private long getVersion() {
		return 2L;
	}

}

class AccountFieldMethodVersion {

	@Score
	private long version = 1L;

	@Score
	private long getVersion() {
		return 2L;
	}

}

class AccountTimestamp {

	@Score
	Timestamp version = Timestamp.from(Instant.now());

}

class AccountInstant {

	@Score
	Instant version = Instant.now();

}

class AccountUnsupportedType {

	@Score
	LocalDate version = LocalDate.now();

}
