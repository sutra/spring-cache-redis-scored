package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.ScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheInterceptor;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.ProxyCachingConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

class ScoredRedisCacheAutoConfigurationTest {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(
			AutoConfigurations.of(
				ProxyCachingConfiguration.class,
				CacheAutoConfiguration.class,
				ScoredRedisCacheAutoConfiguration.class
			)
		);

	@Test
	void test() {
		this.contextRunner
			.withBean(LettuceConnectionFactory.class)
			.run(context -> {
				assertThat(context)
					.hasSingleBean(RedisCacheManagerBuilderCustomizer.class)
					.hasSingleBean(ScoredRedisCacheInterceptor.class)
					.hasSingleBean(ScoreHolder.class);

				Object cacheWriter = context.getBean(CacheManager.class).getCache("hello").getNativeCache();
				assertTrue(cacheWriter instanceof ScoredRedisCacheWriter);
			});
	}

}
