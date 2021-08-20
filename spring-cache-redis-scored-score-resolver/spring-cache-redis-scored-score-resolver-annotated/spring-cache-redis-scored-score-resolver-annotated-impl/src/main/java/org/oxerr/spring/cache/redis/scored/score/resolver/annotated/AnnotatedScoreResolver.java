package org.oxerr.spring.cache.redis.scored.score.resolver.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class AnnotatedScoreResolver implements ScoreResolver {

	private static final long serialVersionUID = 2021082001L;

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

		if (!methods.isEmpty()) {
			final Method method;
			if (methods.size() == 1) {
				method = methods.get(0);
			} else {
				method = methods.stream()
					.map(OrderedAnnotatedElement::new)
					.sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
					.findFirst()
					.map(OrderedAnnotatedElement::getAnnotatedElement)
					.orElseThrow(IllegalArgumentException::new);
			}
			version = this.invokeMethod(method, value);
		} else {
			final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(valueType, annotationType);
			if (!fields.isEmpty()) {
				final Field field;
				if (fields.size() == 1) {
					field = fields.get(0);
				} else {
					field = fields.stream()
						.map(OrderedAnnotatedElement::new)
						.sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
						.findFirst()
						.map(OrderedAnnotatedElement::getAnnotatedElement)
						.orElseThrow(IllegalArgumentException::new);
				}
				version = FieldUtils.readField(field, value, true);
			}
		}

		return Optional.ofNullable(extractScore(version));
	}

	private class OrderedAnnotatedElement<T extends AnnotatedElement> implements Ordered {

		private final T annotatedElement;
		private final int order;

		public OrderedAnnotatedElement(T annotatedElement) {
			this.annotatedElement = annotatedElement;
			this.order = Optional.ofNullable(annotatedElement.getAnnotation(Order.class))
				.map(Order::value)
				.orElse(Ordered.LOWEST_PRECEDENCE);
		}

		public T getAnnotatedElement() {
			return annotatedElement;
		}

		@Override
		public int getOrder() {
			return order;
		}

	}

	private Object invokeMethod(Method method, Object value)
			throws IllegalAccessException, InvocationTargetException {
		method.setAccessible(true);
		return method.invoke(value);
	}

	protected Double extractScore(Object version) {
		return ScoreUtils.extractScore(version);
	}

}
