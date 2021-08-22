package org.oxerr.spring.cache.redis.scored;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class InheritableThreadLocalScoreHolderTest {

	@Test
	void test() {
		InheritableThreadLocalScoreHolder scoreHolder = new InheritableThreadLocalScoreHolder();
		Double score = Double.valueOf(1);

		scoreHolder.set(score);
		assertEquals(score, scoreHolder.get());

		scoreHolder.remove();
		assertNull(scoreHolder.get());
	}

}
