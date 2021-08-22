package org.oxerr.spring.cache.redis.scored.example.spring.data.redis.cache;

public interface BookRepository {

	Book getByIsbn(String isbn);

	Book saveBook(Book book);

}
