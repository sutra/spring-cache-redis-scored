package org.oxerr.spring.cache.redis.scored;

import java.io.Serializable;

public interface ScoreHolder extends Serializable {

	Double get();

	void set(Double score);

	void remove();

}
