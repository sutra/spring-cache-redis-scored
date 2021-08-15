package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheManager;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
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
			});
	}

}
