package org.oxerr.spring.cache.redis.scored.example.helloworld;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(AppRunner.class);

	private final RedisCache cache;

	private final BookRepository bookRepository;

	public AppRunner(CacheManager cacheManager, BookRepository bookRepository) {
		this.bookRepository = bookRepository;

		this.cache = (RedisCache) cacheManager.getCache("books");

		if (this.cache == null) {
			throw new IllegalArgumentException("Cache books should not be null.");
		}

		log.info("Cache type: {}", this.cache.getClass());
		log.info("Native cache: {}", this.cache.getNativeCache());
	}

	@Override
	public void run(String... args) throws Exception {
		// Clear cache before test.
		this.clearCache();
		this.stat();

		String isbn = "isbn-1234";

		this.saveBook(isbn);
		this.stat();

		this.fetch(isbn);
		this.stat();

		this.clearCache();
		this.stat();

		this.fetch(isbn);
		this.stat();
	}

	private void saveBook(String isbn) {
		this.sep(">");
		log.info("Saving book...");
		Book book = bookRepository.save(new Book(isbn, "version 0"));
		log.info("Book saved: {}.", book);

		for (int i = 1; i <= 3; i++) {
			log.info("Updating book...");
			book.setTitle("version " + i);
			book = bookRepository.save(book);
			log.info("Book updated: {}.", book);
		}
		this.sep("<");
	}

	private void fetch(String isbn) {
		this.sep(">");
		log.info("Fetching books...");
		for (int i = 0; i < 3; i++) {
			long startTime = System.nanoTime();

			log.info("Getting book...");
			Book book = bookRepository.getByIsbn(isbn);
			log.info("{} -->{}", isbn, book);

			long elapsedNanos = System.nanoTime() - startTime;

			log.info("Elapsed: {}", Duration.ofNanos(elapsedNanos));
		}
		this.sep("<");
	}

	private void stat() {
		this.sep(">");
		CacheStatistics cacheStatistics = cache.getStatistics();
		log.info("Hits: {}.", cacheStatistics.getHits());
		log.info("Puts: {}.", cacheStatistics.getPuts());
		this.sep("<");
	}

	private void clearCache() {
		log.info("Clearing cache...");
		cache.clear();
		log.info("Cache cleared.");
	}

	private void sep(String s) {
		log.info("{}", StringUtils.repeat(s, 80));
	}

}
