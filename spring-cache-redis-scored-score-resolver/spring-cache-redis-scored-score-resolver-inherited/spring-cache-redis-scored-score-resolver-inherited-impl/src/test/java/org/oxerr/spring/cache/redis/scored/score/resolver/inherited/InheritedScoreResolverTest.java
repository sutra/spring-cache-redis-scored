package org.oxerr.spring.cache.redis.scored.score.resolver.inherited;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable.Versioned;

class InheritedScoreResolverTest {

	private final InheritedScoreResolver scoreResolver = new InheritedScoreResolver();

	@Test
	void testResolveScore() {
		assertEquals(123L, this.scoreResolver.resolveScore(new Book()).get());
		assertEquals(Optional.empty(), this.scoreResolver.resolveScore(new Object()));
	}

}

class Book implements Versioned<Long> {

	@Override
	public Long getVersion() {
		return 123L;
	}

}
