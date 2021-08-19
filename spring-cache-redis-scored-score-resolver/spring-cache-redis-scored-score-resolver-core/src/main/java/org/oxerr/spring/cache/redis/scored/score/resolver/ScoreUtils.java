package org.oxerr.spring.cache.redis.scored.score.resolver;

import java.sql.Timestamp;
import java.time.Instant;

public final class ScoreUtils {

	private ScoreUtils() {
		throw new AssertionError("No " + getClass() + " instances for you!");
	}

	public static Double extractScore(Object version) {
		final Double score;

		if (version == null) {
			score = null;
		} else if (version instanceof Number) {
			Number number = (Number) version;
			score = number.doubleValue();
		} else if (version instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) version;
			long millis = timestamp.getTime();
			int nanos = timestamp.getNanos();
			score = Double.parseDouble(String.format("%d.%d", millis, nanos));
		} else if (version instanceof Instant) {
			Instant instant = (Instant) version;
			long millis = instant.toEpochMilli();
			int nanos = instant.getNano();
			score = Double.parseDouble(String.format("%d.%d", millis, nanos));
		} else {
			throw new IllegalArgumentException("Unsupported type: " + version.getClass());
		}

		return score;
	}

}
