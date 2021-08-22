package org.oxerr.spring.cache.redis.scored.spring.boot.autoconfigure;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.oxerr.spring.cache.redis.scored.InheritableThreadLocalScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoreHolder;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheInterceptor;
import org.oxerr.spring.cache.redis.scored.ScoredRedisCacheWriter;
import org.oxerr.spring.cache.redis.scored.score.resolver.DefaultScoreResolver;
import org.oxerr.spring.cache.redis.scored.score.resolver.ScoreResolver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * See {@code org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration}.
 *
 * <a href="https://docs.spring.io/spring-boot/docs/2.5.3/reference/htmlsingle/#features.caching.provider.redis">
 * Spring Boot Features - Caching - Providers - Redis
 * </a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ScoredRedisCacheInterceptor.class, ScoredRedisCacheWriter.class })
@AutoConfigureAfter(RedisAutoConfiguration.class)
class ScoredRedisCacheAutoConfiguration {

	@Bean
	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
		@NonNull final RedisConnectionFactory connectionFactory,
		@NonNull final ScoreHolder scoreHolder,
		@Nullable final ClassLoader classLoader
	) {
		return builder -> {
			final ScoredRedisCacheWriter cacheWriter = new ScoredRedisCacheWriter(
				connectionFactory,
				CacheStatisticsCollector.none(),
				scoreHolder,
				this.getRedisCacheWriter(builder)
			);
			builder.cacheWriter(cacheWriter);
		};
	}

	@Bean
	public ScoredRedisCacheInterceptor scoredRedisCacheInterceptor(
		final BeanFactoryCacheOperationSourceAdvisor cacheOperationSourceAdvisor,
		final CacheOperationSource cacheOperationSource,
		final ScoreHolder scoreHolder
	) {
		final ScoreResolver scoreResolver = new DefaultScoreResolver();

		final ScoredRedisCacheInterceptor scoredRedisCacheInterceptor = new ScoredRedisCacheInterceptor(scoreResolver, scoreHolder);
		scoredRedisCacheInterceptor.setCacheOperationSource(cacheOperationSource);

		cacheOperationSourceAdvisor.setAdvice(scoredRedisCacheInterceptor);

		return scoredRedisCacheInterceptor;
	}

	@Bean
	public ScoreHolder scoreHolder() {
		return new InheritableThreadLocalScoreHolder();
	}

	private RedisCacheWriter getRedisCacheWriter(RedisCacheManagerBuilder builder) {
		return this.readDeclaredField(builder, "cacheWriter");
	}

	@SuppressWarnings("unchecked")
	private <T> T readDeclaredField(RedisCacheManagerBuilder builder, String fieldName) {
		try {
			return (T) FieldUtils.readDeclaredField(builder, fieldName, true);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
