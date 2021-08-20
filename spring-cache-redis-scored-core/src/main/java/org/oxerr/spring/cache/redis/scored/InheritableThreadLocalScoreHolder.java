package org.oxerr.spring.cache.redis.scored;

public class InheritableThreadLocalScoreHolder implements ScoreHolder {

	private static final long serialVersionUID = 2021082001L;

	private static final InheritableThreadLocal<Double> score = new InheritableThreadLocal<>();

	@Override
	public Double get() {
		return score.get();
	}

	@Override
	public void set(Double value) {
		score.set(value);
	}

	@Override
	public void remove() {
		score.remove();
	}

}
