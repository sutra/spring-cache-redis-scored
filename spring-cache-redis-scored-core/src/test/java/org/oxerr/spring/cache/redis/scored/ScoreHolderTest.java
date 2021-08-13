package org.oxerr.spring.cache.redis.scored;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ScoreHolderTest {

	@Test
	void test() {
		Double score = Double.valueOf(1);

		ScoreHolder.set(score);
		assertEquals(score, ScoreHolder.get());

		ScoreHolder.remove();
		assertNull(ScoreHolder.get());
	}

}
