package org.oxerr.spring.cache.redis.scored.example.spring.cache.redis.scored;

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
	}

	@Override
	public void run(String... args) throws Exception {
		// Clear cache before test.
		cache.clear();

		logger.info(".... Testing");
		logger.info("Cache type: {}", cache.getClass());

		final String isbn = "isbn-1234";
		final String key = isbn;

		final Book book = bookRepository.saveBook(new Book(isbn, "version 1", 1L));
		logger.info("Book: {}", book);

		// 1. The cached data is expired.
		// Clear cache to mock the cached data is expired.
		logger.info("1. The cached data is expired.");
		cache.clear();

		// Request A querying data.
		final Thread threadA = new Thread(() -> {

			// 2. Request A queries data from persistence layer, got old data(version 1).
			logger.info("2. Request A queries data from persistence layer, got old data(version 1).");
			Book oldData = bookRepository.getByIsbn(isbn);
			logger.info("2. Request A queries data from persistence layer, got old data(version 1). Old data: {}", oldData);

			// 5. Request A adds old data(version 1) into cache with score 1.
			logger.info("5. Request A adds old data(version 1) into cache.");
			Cache.ValueWrapper cachedData = cache.get(key);
			logger.info("5. Request A adds old data(version 1) into cache. Cached data is fresh: {}", cachedData.get());

		}, "Request A");

		// Request B updating data.
		final Thread threadB = new Thread(() -> {

			// 3. Request B writes new data(version 2) into persistence layer.
			logger.info("3. Request B writes new data(version 2) into persistence layer.");
			Book newData = bookRepository.saveBook(new Book(isbn, "version 2", 2L));
			logger.info("3. Request B writes new data(version 2) into persistence layer. New data: {}", newData);

			// Request B writes new data(version 2) into cache with score 2.
			logger.info("4. Request B writes new data(version 2) into cache with score 2.");
			Cache.ValueWrapper cachedData = cache.get(key);
			logger.info("4. Request B writes new data(version 2) into cache with score 2. Cached data is fresh: {}", cachedData.get());

		}, "Request B");

		threadA.start();
		Thread.sleep(1000);
		threadB.start();

		threadA.join();
		threadB.join();

		// Now we have 2 versions of data in cache,
		// the version 1 is score = 1,
		// and the version 2 is score = 2.
		// And when querying from cache,
		// the newest version with maximum score(score = 2, version = 2)
		// of data will be returned,
		// by using Redis command ZREVRANGEBYSCORE key +inf -inf limit 0 1.
		logger.info(".... Now we have 2 versions of data in cache.");
		logger.info("Fresh data returned: {}", bookRepository.getByIsbn(isbn));
	}

}
