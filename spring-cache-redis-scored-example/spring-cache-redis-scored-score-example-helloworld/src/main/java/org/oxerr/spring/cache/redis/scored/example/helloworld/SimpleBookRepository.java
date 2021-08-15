package org.oxerr.spring.cache.redis.scored.example.helloworld;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SimpleBookRepository implements BookRepository {

	private final Map<String, Book> books = new HashMap<>();

	@Override
	@Cacheable("books")
	public Book getByIsbn(String isbn) {
		Book book = this.books.get(isbn);

		simulateSlowService();

		return book;
	}

	@Override
	@CachePut(value = "books", key = "#result.isbn")
	public Book saveBook(Book book) {
		this.books.put(book.getIsbn(), book);
		return book;
	}

	// Don't do this at home
	private void simulateSlowService() {
		try {
			long time = 3000L;
			Thread.sleep(time);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		}
	}

}
