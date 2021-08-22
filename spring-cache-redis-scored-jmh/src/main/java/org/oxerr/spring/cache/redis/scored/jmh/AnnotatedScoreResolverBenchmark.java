package org.oxerr.spring.cache.redis.scored.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.oxerr.spring.cache.redis.scored.score.resolver.annotated.AnnotatedScoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Version;

@State(Scope.Benchmark)
public class AnnotatedScoreResolverBenchmark {

	private static final Logger log = LoggerFactory.getLogger(AnnotatedScoreResolverBenchmark.class);

	private final AnnotatedScoreResolver annotatedScoreResolver;
	private final Book book;

	public AnnotatedScoreResolverBenchmark() {
		this.annotatedScoreResolver = new AnnotatedScoreResolver(Version.class);
		this.book = new Book("isbn-1234", "version 1", 1L);
	}

	@Benchmark
	public void testResolveScore() {
		this.annotatedScoreResolver.resolveScore(this.book);
	}

	public static void main(String[] args) {
		AnnotatedScoreResolverBenchmark benchmark = new AnnotatedScoreResolverBenchmark();

		for (int i = 0; i < 100; i++) {
			long startTime = System.nanoTime();
			// ... the code being measured ...
			benchmark.testResolveScore();
			long elapsedNanos = System.nanoTime() - startTime;

			log.info("Elapsed nanos: {}", elapsedNanos);
		}
	}

}
