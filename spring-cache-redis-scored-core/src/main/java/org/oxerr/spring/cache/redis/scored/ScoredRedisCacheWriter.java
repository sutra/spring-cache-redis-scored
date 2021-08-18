package org.oxerr.spring.cache.redis.scored;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.ZAddArgs;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ScoredRedisCacheWriter implements RedisCacheWriter {

	private static final String MUST_NOT_BE_NULL = " must not be null!";

	private static final String NAME_NOT_NULL = "Name" + MUST_NOT_BE_NULL;
	private static final String KEY_NOT_NULL = "Key" + MUST_NOT_BE_NULL;
	private static final String VALUE_NOT_NULL = "Value" + MUST_NOT_BE_NULL;

	private static final String OK = "OK";

	private static final Double DEFAULT_SCORE = Double.valueOf(0d);

	private final RedisConnectionFactory connectionFactory;
	private final CacheStatisticsCollector statistics;
	private final ScoreHolder scoreHolder;
	private final RedisCacheWriter cacheWriter;

	public ScoredRedisCacheWriter(
		@NonNull RedisConnectionFactory connectionFactory
	) {
		this(connectionFactory, CacheStatisticsCollector.none());
	}

	public ScoredRedisCacheWriter(
		@NonNull RedisConnectionFactory connectionFactory,
		@NonNull CacheStatisticsCollector cacheStatisticsCollector
	) {
		this(
			connectionFactory,
			cacheStatisticsCollector,
			new InheritableThreadLocalScoreHolder()
		);
	}

	public ScoredRedisCacheWriter(
		@NonNull RedisConnectionFactory connectionFactory,
		@NonNull CacheStatisticsCollector cacheStatisticsCollector,
		@NonNull ScoreHolder scoreHolder
	) {
		this(
			connectionFactory,
			cacheStatisticsCollector,
			scoreHolder,
			RedisCacheWriter
				.nonLockingRedisCacheWriter(connectionFactory)
				.withStatisticsCollector(cacheStatisticsCollector)
		);
	}

	public ScoredRedisCacheWriter(
		@NonNull RedisConnectionFactory connectionFactory,
		@NonNull CacheStatisticsCollector cacheStatisticsCollector,
		@NonNull ScoreHolder scoreHolder,
		@NonNull RedisCacheWriter cacheWriter
	) {
		this.connectionFactory = connectionFactory;
		this.statistics = cacheStatisticsCollector;
		this.scoreHolder = scoreHolder;
		this.cacheWriter = cacheWriter;
	}

	@Override
	public CacheStatistics getCacheStatistics(String cacheName) {
		return statistics.getCacheStatistics(cacheName);
	}

	@Override
	public void put(String name, byte[] key, byte[] value, Duration ttl) {
		Assert.notNull(name, NAME_NOT_NULL);
		Assert.notNull(key, KEY_NOT_NULL);
		Assert.notNull(value, VALUE_NOT_NULL);

		final double score = getScore();

		final long millis = ttl.toMillis();
		final Range range = Range.range().lt(score);

		execute(name, connection -> {

			connection.zAdd(key, score, value);
			connection.zRemRangeByScore(key, range);

			if (shouldExpireWithin(ttl)) {
				connection.pExpire(key, millis);
			}

			return OK;
		});

		statistics.incPuts(name);
	}

	@Override
	public byte[] get(String name, byte[] key) {
		Assert.notNull(name, NAME_NOT_NULL);
		Assert.notNull(key, KEY_NOT_NULL);

		final Range range = Range.unbounded();
		final Limit limit = Limit.limit().count(1);

		byte[] result = execute(name, connection -> connection.zRevRangeByScore(key, range, limit))
			.stream()
			.findFirst()
			.orElse(null);

		statistics.incGets(name);

		if (result != null) {
			statistics.incHits(name);
		} else {
			statistics.incMisses(name);
		}

		return result;
	}

	@Override
	public byte[] putIfAbsent(String name, byte[] key, byte[] value, Duration ttl) {
		Assert.notNull(name, NAME_NOT_NULL);
		Assert.notNull(key, KEY_NOT_NULL);
		Assert.notNull(value, VALUE_NOT_NULL);

		final double score = getScore();

		final long millis = ttl.toMillis();
		final Range range = Range.range().lt(score);
		final Limit limit = Limit.limit().count(1);

		return execute(name, connection -> {
			final Boolean added = connection.zAdd(key, score, value, ZAddArgs.ifNotExists());
			connection.zRemRangeByScore(key, range);

			if (shouldExpireWithin(ttl)) {
				connection.pExpire(key, millis);
			}

			if (Boolean.TRUE.equals(added)) {
				statistics.incPuts(name);
				return null;
			}

			return connection.zRevRangeByScore(key, range, limit)
				.stream()
				.findFirst()
				.orElse(null);
		});
	}

	@Override
	public void remove(String name, byte[] key) {
		this.cacheWriter.remove(name, key);
	}

	@Override
	public void clean(String name, byte[] pattern) {
		this.cacheWriter.clean(name, pattern);
	}

	@Override
	public void clearStatistics(String name) {
		statistics.reset(name);
	}

	@Override
	public ScoredRedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
		return new ScoredRedisCacheWriter(connectionFactory, cacheStatisticsCollector);
	}

	private <T> T execute(String name, Function<RedisConnection, T> callback) {
		try (RedisConnection connection = connectionFactory.getConnection()) {
			return callback.apply(connection);
		}
	}

	private static boolean shouldExpireWithin(@Nullable Duration ttl) {
		return ttl != null && !ttl.isZero() && !ttl.isNegative();
	}

	private double getScore() {
		final Double score = Optional.ofNullable(this.scoreHolder.get())
			.orElse(DEFAULT_SCORE);
		return score.doubleValue();
	}

}
