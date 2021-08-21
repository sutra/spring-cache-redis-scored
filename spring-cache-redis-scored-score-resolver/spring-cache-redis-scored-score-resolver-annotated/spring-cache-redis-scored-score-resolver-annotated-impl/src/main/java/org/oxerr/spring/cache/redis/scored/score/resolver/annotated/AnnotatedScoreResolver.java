package org.oxerr.spring.cache.redis.scored.score.resolver.annotated;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	private transient Map<Class<?>, AnnotatedElementWrapper<? extends AnnotatedElement>> annotatedElements;

	private Class<? extends Annotation> annotationType;

	public AnnotatedScoreResolver(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
		this.annotatedElements = new HashMap<>();
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
			score = this.getScoreInternal(value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}

		return score;
	}

	private Optional<Double> getScoreInternal(@NonNull Object value)
			throws IllegalAccessException, InvocationTargetException {
		final Class<?> valueType = value.getClass();
		final Object version = this.getAnnotatedElement(valueType).readValue(value);
		return Optional.ofNullable(extractScore(version));
	}

	private AnnotatedElementWrapper<? extends AnnotatedElement> getAnnotatedElement(Class<?> valueType) {
		AnnotatedElementWrapper<? extends AnnotatedElement> annotatedElement = this.annotatedElements.get(valueType);
		if (annotatedElement == null) {
			annotatedElement = resolveAnnotatedElement(valueType);
			this.annotatedElements.put(valueType, annotatedElement);
		}
		return annotatedElement;
	}

	private AnnotatedElementWrapper<? extends AnnotatedElement> resolveAnnotatedElement(Class<?> valueType) {
		final AnnotatedElementWrapper<? extends AnnotatedElement> annotatedElement;

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
			annotatedElement = AnnotatedElementWrapper.of(method, AnnotatedElementWrapper.Type.METHOD);
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
				annotatedElement = AnnotatedElementWrapper.of(field, AnnotatedElementWrapper.Type.FIELD);
			} else {
				annotatedElement = AnnotatedElementWrapper.empty();
			}
		}

		return annotatedElement;
	}

	private static class AnnotatedElementWrapper<T extends AnnotatedElement> {

		/**
		 * Common instance for {@code empty()}.
		 */
		private static final AnnotatedElementWrapper<?> EMPTY = new AnnotatedElementWrapper<>();

		private final T annotatedElement;

		public enum Type {
			METHOD,
			FIELD;
		}

		private final Type type;

		private AnnotatedElementWrapper() {
			this.annotatedElement = null;
			this.type = null;
		}

		public static <T extends AnnotatedElement> AnnotatedElementWrapper<T> empty() {
			@SuppressWarnings("unchecked")
			AnnotatedElementWrapper<T> t = (AnnotatedElementWrapper<T>) EMPTY;
			return t;
		}

		private AnnotatedElementWrapper(T annotatedElement, Type type) {
			this.annotatedElement = Objects.requireNonNull(annotatedElement);
			this.type = Objects.requireNonNull(type);
		}

		public static <T extends AnnotatedElement> AnnotatedElementWrapper<T> of(T value, Type type) {
			return new AnnotatedElementWrapper<>(value, type);
		}

		private boolean isPresent() {
			return this.annotatedElement != null;
		}

		public Object readValue(Object object) throws IllegalAccessException, InvocationTargetException {
			Object value;
			if (isPresent()) {
				if (this.type == Type.METHOD) {
					value = this.invokeMethod((Method) this.annotatedElement, object);
				} else {
					value = FieldUtils.readField((Field) this.annotatedElement, object, true);
				}
			} else {
				value = null;
			}
			return value;
		}

		private Object invokeMethod(Method method, Object value)
				throws IllegalAccessException, InvocationTargetException {
			method.setAccessible(true);
			return method.invoke(value);
		}

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

	protected Double extractScore(Object version) {
		return ScoreUtils.extractScore(version);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeUTF(this.annotationType.getName());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.annotationType = (Class<? extends Annotation>) Class.forName(in.readUTF());
		this.annotatedElements = new HashMap<>();
	}

}
