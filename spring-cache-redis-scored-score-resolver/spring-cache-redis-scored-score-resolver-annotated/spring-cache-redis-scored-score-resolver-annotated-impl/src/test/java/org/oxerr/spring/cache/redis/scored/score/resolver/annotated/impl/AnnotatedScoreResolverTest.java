package org.oxerr.spring.cache.redis.scored.score.resolver.annotated.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.AnnotatedScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.annotation.Score;
import org.springframework.core.annotation.Order;

class AnnotatedScoreResolverTest {

	private final AnnotatedScoreResolver scoreResolver = new AnnotatedScoreResolver(Score.class);

	@Test
	void testSerialization() throws IllegalArgumentException, IllegalAccessException {
		scoreResolver.resolveScore(new Object() {
		});

		Field annotatedElementsField = FieldUtils.getDeclaredField(scoreResolver.getClass(), "annotatedElements", true);
		Field annotationTypeField = FieldUtils.getDeclaredField(scoreResolver.getClass(), "annotationType", true);

		@SuppressWarnings("unchecked")
		Map<Class<?>, ?> annotatedElementsBefore = (Map<Class<?>, ?>) annotatedElementsField.get(scoreResolver);
		@SuppressWarnings("unchecked")
		Class<? extends Annotation> annotationTypeBefore = (Class<? extends Annotation>) annotationTypeField.get(scoreResolver);

		assertNotNull(annotatedElementsBefore);
		assertNotNull(annotationTypeBefore);

		assertEquals(1, annotatedElementsBefore.size());

		AnnotatedScoreResolver deserialized = SerializationUtils.roundtrip(scoreResolver);

		@SuppressWarnings("unchecked")
		Map<Class<?>, ?> annotatedElementsAfter = (Map<Class<?>, ?>) annotatedElementsField.get(deserialized);
		@SuppressWarnings("unchecked")
		Class<? extends Annotation> annotationTypeAfter = (Class<? extends Annotation>) annotationTypeField.get(deserialized);

		assertNotNull(annotatedElementsAfter);
		assertNotNull(annotationTypeAfter);

		assertEquals(0, annotatedElementsAfter.size());

		assertEquals(annotationTypeBefore, annotationTypeAfter);
	}

	@Test
	void testResolveScoreNull() {
		assertEquals(Optional.empty(), scoreResolver.resolveScore(null));
	}

	@Test
	void testResolveScoreFromMethod() {
		assertEquals(1, scoreResolver.resolveScore(new Object() {

			@Score
			private long getVersion() {
				return 1L;
			}

		}).get().longValue());
	}

	@Test
	void testResolveScoreFromMethodFirst() {
		assertEquals(2, scoreResolver.resolveScore(new Object() {

			@Score
			private long version = 1L;

			@Score
			private long getVersion() {
				return 2L;
			}

		}).get().longValue());
	}

	@Test
	void testResolveScoreFromMethodNullAndFieldGiveUp() {
		assertEquals(Optional.empty(), this.scoreResolver.resolveScore(new Object() {

			@Score
			private long version = 1;

			@Score
			private Long getVersion2() {
				return null;
			}

		}));
	}

	@Test
	void testResolveScoreFromField() {
		assertEquals(1, scoreResolver.resolveScore(new Object() {

			@Score
			private long version = 1L;

		}).get().longValue());
	}

	@Test
	void testResolveScoreFromMethodOrder() {
		assertEquals(1L, this.scoreResolver.resolveScore(new Object() {

			@Score
			@Order(0)
			private long getVersion() {
				return 1L;
			}

			@Score
			@Order(1)
			private long getVersion2() {
				return 2L;
			}

		}).get().longValue());
	}

	@Test
	void testResolveScoreFromFieldOrder() {
		assertEquals(1L, this.scoreResolver.resolveScore(new Object() {

			@Score
			@Order(0)
			private long version = 1L;

			@Score
			@Order(1)
			private long version2 = 2L;

		}).get().longValue());
	}

	@Test
	void testResolveScoreOrderDefault() {
		assertEquals(2L, this.scoreResolver.resolveScore(new Object() {

			@Score
			private long version = 1L;

			@Score
			@Order(1)
			private long version2 = 2L;

		}).get().longValue());
	}

	@Test
	void testResolveScoreTypeTimestamp() {
		Timestamp now = Timestamp.from(Instant.now());
		assertEquals(
			Double.parseDouble(String.format("%d.%d", now.getTime(), now.getNanos())),
			this.scoreResolver.resolveScore(new Object() {

				@Score
				private Timestamp version = now;

			}).get()
		);
	}

	@Test
	void testResolveScoreTypeInstant() {
		Instant now = Instant.now();
		assertEquals(
			Double.parseDouble(String.format("%d.%d", now.toEpochMilli(), now.getNano())),
			this.scoreResolver.resolveScore(new Object() {

				@Score
				private Instant version = now;

			}).get()
		);
	}

	@Test
	void testResolveScoreTypeUnsupported() {
		Object o = new Object() {

			@Score
			private LocalDate version = LocalDate.now();

		};
		assertThrows(IllegalArgumentException.class, () -> this.scoreResolver.resolveScore(o));
	}

}
