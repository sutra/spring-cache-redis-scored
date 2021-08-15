package org.oxerr.spring.cache.redis.scored.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.oxerr.spring.cache.redis.scored.example.spring.cache.redis.scored.Book;
import org.oxerr.spring.cache.redis.scored.score.resolver.inherited.InheritedScoreResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
public class InheritedScoreResolverBenchmark {

	private static final Logger log = LoggerFactory.getLogger(InheritedScoreResolverBenchmark.class);

	private final InheritedScoreResolver inheritedScoreResolver;
	private final Book book;

	public InheritedScoreResolverBenchmark() {
		this.inheritedScoreResolver = new InheritedScoreResolver();
		this.book = new Book("isbn-1234", "version 1", 1L);
	}

	@Benchmark
	public void testResolveScore() {
		this.inheritedScoreResolver.resolveScore(this.book);
	}

	public static void main(String[] args) {
		InheritedScoreResolverBenchmark benchmark = new InheritedScoreResolverBenchmark();

		for (int i = 0; i < 100; i++) {
			long startTime = System.nanoTime();
			// ... the code being measured ...
			benchmark.testResolveScore();
			long elapsedNanos = System.nanoTime() - startTime;

			log.info("elapsedNanos: {}", elapsedNanos);
		}
	}

}
