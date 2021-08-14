package org.oxerr.spring.cache.redis.scored.score.resolver.annotated;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.oxerr.spring.cache.redis.scored.ChainedScoreResolver;
import org.oxerr.spring.cache.redis.scored.ScoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

public class ChainedAnnotatedScoreResolver implements ScoreResolver {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final ChainedScoreResolver chainedScoreResolver;

	public ChainedAnnotatedScoreResolver(String... annotationTypes) {
		List<AnnotatedScoreResolver> resolvers = this.getAnnotatedScoreResolvers(annotationTypes);
		this.chainedScoreResolver = new ChainedScoreResolver(resolvers);
	}

	@Override
	public Optional<Double> resolveScore(@Nullable Object value) {
		return this.chainedScoreResolver.resolveScore(value);
	}

	private List<AnnotatedScoreResolver> getAnnotatedScoreResolvers(String... annotationTypes) {
		return Arrays.stream(annotationTypes)
			.map(this::getAnnotatedScoreResolver)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private AnnotatedScoreResolver getAnnotatedScoreResolver(String className) {
		try {
			Class<? extends Annotation> annotationType = getAnnotationType(className);
			return new AnnotatedScoreResolver(annotationType);
		} catch (ClassNotFoundException e) {
			log.info("Class not found: {}", e.getMessage());
			return null;
		}
	}

	private Class<? extends Annotation> getAnnotationType(String className) throws ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends Annotation> annotationType = (Class<? extends Annotation>) Class.forName(className);
		return annotationType;
	}

}
