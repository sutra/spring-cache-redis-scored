package org.oxerr.spring.cache.redis.scored.score.resolver.inherited;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.inheritable.Scored;

class InheritedScoreResolverTest {

	private final InheritedScoreResolver scoreResolver = new InheritedScoreResolver();

	@Test
	void testResolveScore() {
		assertEquals(123d, this.scoreResolver.resolveScore(new Book()).get());
		assertEquals(Optional.empty(), this.scoreResolver.resolveScore(new Object()));
	}

}

class Book implements Scored {

	@Override
	public Double getScore() {
		return 123d;
	}

}
