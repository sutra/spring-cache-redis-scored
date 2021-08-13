package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DefaultScoreResolverTest {

	private final DefaultScoreResolver resolver = new DefaultScoreResolver();

	@Test
	void testResolveScore() {
		assertEquals(1, this.resolver.resolveScore(new ScoredAccount()).get().longValue());
		assertEquals(1, this.resolver.resolveScore(new VersionedAccount()).get().longValue());
		assertEquals(1, this.resolver.resolveScore(new SpringDataVersionedAccount()).get().longValue());
	}

}

class ScoredAccount {

	@org.oxerr.spring.cache.redis.scored.score.resolver.annotation.Score
	private long version = 1L;

}

class VersionedAccount {

	@javax.persistence.Version
	private long version = 1L;

}

class SpringDataVersionedAccount {

	@org.springframework.data.annotation.Version
	private long version = 1L;

}
