package org.oxerr.spring.cache.redis.scored.serializer;

import java.util.Optional;

import org.oxerr.spring.cache.redis.scored.ScoreHolder;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class ScoredRedisSerializer implements RedisSerializer<Object> {

	private final RedisSerializer<Object> serializer;
	private final ScoreResolver scoreResolver;
	private final ScoreHolder scoreHolder;

	public ScoredRedisSerializer(
		@NonNull RedisSerializer<Object> serializer,
		@NonNull ScoreResolver scoreResolver,
		@NonNull ScoreHolder scoreHolder
	) {
		this.serializer = serializer;
		this.scoreResolver = scoreResolver;
		this.scoreHolder = scoreHolder;
	}

	@Override
	@Nullable
	public byte[] serialize(@Nullable Object t) throws SerializationException {
		this.serializeScore(t);
		return this.serializer.serialize(t);
	}

	@Override
	@Nullable
	public Object deserialize(@Nullable byte[] bytes) throws SerializationException {
		return this.serializer.deserialize(bytes);
	}

	protected void serializeScore(@Nullable Object t) {
		Optional<Double> score = this.scoreResolver.resolveScore(t);
		this.scoreHolder.set(score.orElse(null));
	}

}
