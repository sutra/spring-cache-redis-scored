package org.oxerr.spring.cache.redis.scored;

public interface ScoreHolder {

	Double get();

	void set(Double score);

	void remove();

}
