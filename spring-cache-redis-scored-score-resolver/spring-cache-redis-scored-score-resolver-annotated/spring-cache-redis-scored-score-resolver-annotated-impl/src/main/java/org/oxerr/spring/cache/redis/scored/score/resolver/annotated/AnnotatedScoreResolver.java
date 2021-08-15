package org.oxerr.spring.cache.redis.scored.score.resolver.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AnnotatedScoreResolver implements ScoreResolver {

	private final Class<? extends Annotation> annotationType;

	public AnnotatedScoreResolver(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public Optional<Double> resolveScore(@Nullable Object value) {
		if (value == null) {
			return Optional.empty();
		} else {
			return getScore(value);
		}
	}

	private Optional<Double> getScore(@NonNull Object value) {
		Optional<Double> score = Optional.empty();

		try {
			score = this.getVersion(value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}

		return score;
	}

	private Optional<Double> getVersion(@NonNull Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?> valueType = value.getClass();

		Object version = null;

		final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(valueType, annotationType, true, true);
		for (final Method method : methods) {
			method.setAccessible(true);
			version = method.invoke(value);
			if (version != null) {
				break;
			}
		}

		if (version == null) {
			final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(valueType, annotationType);
			for (final Field field : fields) {
				field.setAccessible(true);
				version = field.get(value);

				if (version != null) {
					break;
				}
			}
		}

		return Optional.ofNullable(extractScore(version));
	}

	private Double extractScore(Object version) {
		final Double score;

		if (version == null) {
			score = null;
		} else if (version instanceof Number) {
			Number number = (Number) version;
			score = number.doubleValue();
		} else if (version instanceof Date) {
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
