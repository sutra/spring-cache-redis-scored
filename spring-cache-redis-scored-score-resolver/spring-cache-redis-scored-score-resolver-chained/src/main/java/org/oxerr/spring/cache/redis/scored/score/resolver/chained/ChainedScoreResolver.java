package org.oxerr.spring.cache.redis.scored.score.resolver.chained;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;

public class ChainedScoreResolver implements ScoreResolver {

	private final List<ScoreResolver> resolvers;

	public ChainedScoreResolver(ScoreResolver... resolvers) {
		this(Arrays.stream(resolvers).collect(Collectors.toList()));
	}

	public ChainedScoreResolver(Iterable<? extends ScoreResolver> resolvers) {
		this(StreamSupport.stream(resolvers.spliterator(), false).collect(Collectors.toList()));
	}

	private ChainedScoreResolver(List<ScoreResolver> resolvers) {
		this.resolvers = resolvers;
	}

	@Override
	public Optional<Double> resolveScore(Object value) {
		Optional<Double> score;

		for (ScoreResolver scoreResolver : this.resolvers) {
			score = scoreResolver.resolveScore(value);
			if (score.isPresent()) {
				return score;
			}
		}

		return Optional.empty();
	}

}
