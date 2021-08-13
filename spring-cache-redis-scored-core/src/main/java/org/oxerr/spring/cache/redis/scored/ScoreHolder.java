package org.oxerr.spring.cache.redis.scored;

class ScoreHolder {

	private static final InheritableThreadLocal<Double> score = new InheritableThreadLocal<>();

	private ScoreHolder() {
		throw new AssertionError("No " + this.getClass() + " instances for you!");
	}

	public static Double get() {
		return score.get();
	}

	public static void set(Double value) {
		score.set(value);
	}

	public static void remove() {
		score.remove();
	}

}
