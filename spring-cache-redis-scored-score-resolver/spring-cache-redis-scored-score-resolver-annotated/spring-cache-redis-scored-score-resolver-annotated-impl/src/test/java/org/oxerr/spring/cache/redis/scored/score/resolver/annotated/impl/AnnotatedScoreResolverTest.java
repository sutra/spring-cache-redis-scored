package org.oxerr.spring.cache.redis.scored.score.resolver.annotated.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.AnnotatedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.annotation.Score;

class AnnotatedScoreResolverTest {

	private final AnnotatedScoreResolver annotatedScoreResolver = new AnnotatedScoreResolver(Score.class);

	@Test
	void testResolveScoreFromField() {
		assertEquals(1, annotatedScoreResolver.resolveScore(new AccountFieldVersion()).get().longValue());
	}

	@Test
	void testResolveScoreFromMethod() {
		assertEquals(2, annotatedScoreResolver.resolveScore(new AccountMethodVersion()).get().longValue());
		assertEquals(2, annotatedScoreResolver.resolveScore(new AccountFieldMethodVersion()).get().longValue());
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
