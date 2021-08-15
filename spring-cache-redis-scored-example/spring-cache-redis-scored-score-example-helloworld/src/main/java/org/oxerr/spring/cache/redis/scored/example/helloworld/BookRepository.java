package org.oxerr.spring.cache.redis.scored.example.helloworld;

public interface BookRepository {

	Book getByIsbn(String isbn);

	Book saveBook(Book book);

}
