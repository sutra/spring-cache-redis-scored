package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCache;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

class ScoredRedisCacheAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(ScoredRedisCacheAutoConfiguration.class));

	@Test
	void test() {
		this.contextRunner
			.withBean(LettuceConnectionFactory.class)
			.run(context -> {
				assertThat(context).hasSingleBean(LettuceConnectionFactory.class);
				assertThat(context).hasSingleBean(ScoredRedisCacheManager.class);

				ScoredRedisCache cache = ((ScoredRedisCache) (context.getBean(CacheManager.class).getCache("hello")));
				assertThat(cache.getCacheConfiguration().getTtl()).isEqualTo(Duration.ZERO);
			});

		this.contextRunner
			.withBean(LettuceConnectionFactory.class)
			.withPropertyValues("spring.cache.redis.time-to-live=11m")
			.run(context -> {
				assertThat(context).hasSingleBean(LettuceConnectionFactory.class);
				assertThat(context).hasSingleBean(ScoredRedisCacheManager.class);

				ScoredRedisCache cache = ((ScoredRedisCache) (context.getBean(CacheManager.class).getCache("hello")));
				assertThat(cache.getCacheConfiguration().getTtl()).isEqualTo(Duration.ofMinutes(11));
			});
	}

}
