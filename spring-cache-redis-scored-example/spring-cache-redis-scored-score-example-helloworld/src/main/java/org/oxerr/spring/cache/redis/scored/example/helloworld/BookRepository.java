package org.oxerr.spring.cache.redis.scored.example.helloworld;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {

	@Cacheable("books")
	Book getByIsbn(String isbn);

	@Override
	@CachePut(value = "books", key = "#result.isbn")
	<S extends Book> S save(S entity);

}
