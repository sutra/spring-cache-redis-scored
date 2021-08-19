package org.oxerr.spring.cache.redis.scored.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.InheritableThreadLocalScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoreHolder;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.data.redis.serializer.RedisSerializer;

class ScoredRedisSerializerTest {

	private final RedisSerializer<Object> internalSerializer = RedisSerializer.java();
	private final ScoreResolver scoreResolver = mock(ScoreResolver.class);
	private final ScoreHolder scoreHolder = new InheritableThreadLocalScoreHolder();
	private final ScoredRedisSerializer serializer = new ScoredRedisSerializer(internalSerializer, scoreResolver, scoreHolder);

	@Test
	void testScoredRedisSerializer() {
		Book book = new Book("hello", 1);
		when(this.scoreResolver.resolveScore(book)).thenReturn(Optional.of(1d));

		byte[] ser = this.serializer.serialize(book);
		Book deser = (Book) this.serializer.deserialize(ser);

		assertEquals(book.getName(), deser.getName());
		assertEquals(book.getVersion(), deser.getVersion());

		assertEquals(1, this.scoreHolder.get().longValue());
	}

}

class Book implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final int version;

	public Book(String name, int version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

}
