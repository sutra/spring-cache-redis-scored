package org.oxerr.spring.cache.redis.scored.example.spring.cache.redis.scored;

public interface BookRepository {

	Book getByIsbn(String isbn);

	Book saveBook(Book book);

}
