package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import org.oxerr.spring.cache.redis.scored.ScoredRedisCache;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheConfiguration;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheManager;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@ConditionalOnClass({ ScoredRedisCache.class, RedisConnectionFactory.class })
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnMissingBean(CacheManager.class)
public class ScoredRedisCacheAutoConfiguration {

	@Bean
	public ScoredRedisCacheManager scoredRedisCacheManager(RedisConnectionFactory connectionFactory) {
		final ScoredRedisCacheWriter scoredRedisCacheWriter = new ScoredRedisCacheWriter(connectionFactory);
		final ScoredRedisCacheConfiguration scoredRedisCacheConfiguration = new ScoredRedisCacheConfiguration(
			RedisCacheConfiguration.defaultCacheConfig(),
			new DefaultScoreResolver()
		);
		return new ScoredRedisCacheManager(scoredRedisCacheWriter, scoredRedisCacheConfiguration);
	}

}
