package org.oxerr.spring.cache.redis.scored.example.helloworld;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

	private final Cache cache;

	private final BookRepository bookRepository;

	public AppRunner(CacheManager cacheManager, BookRepository bookRepository) {
		this.bookRepository = bookRepository;

		this.cache = cacheManager.getCache("books");

		if (this.cache == null) {
			throw new IllegalArgumentException("Cache books should not be null.");
		}

		logger.info("Cache type: {}", this.cache.getClass());
		logger.info("Native cache: {}", this.cache.getNativeCache());
	}

	@Override
	public void run(String... args) throws Exception {
		// Clear cache before test.
		cache.clear();

		final Book book1234 = bookRepository.saveBook(new Book("isbn-1234", "version 1", 1L));
		logger.info("Book1234: {}", book1234);

		final Book book4567 = bookRepository.saveBook(new Book("isbn-4567", "version 1", 1L));
		logger.info("Book4567: {}", book4567);

		// Clear cache for test.
		cache.clear();

		logger.info(".... Fetching books");
		for (int i = 0; i < 3; i++) {
			long startTime = System.nanoTime();

			logger.info("isbn-1234 -->{}", bookRepository.getByIsbn("isbn-1234"));
			logger.info("isbn-4567 -->{}", bookRepository.getByIsbn("isbn-4567"));

			long elapsedNanos = System.nanoTime() - startTime;

			logger.info("elapsed: {}", Duration.ofNanos(elapsedNanos));
		}
	}

}
