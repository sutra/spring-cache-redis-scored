package org.oxerr.spring.cache.redis.scored.example.helloworld;

import javax.persistence.Version;

public class Book {

	private String isbn;
	private String title;

	@Version
	private Long version;

	public Book() {
	}

	public Book(String isbn, String title, Long version) {
		this.isbn = isbn;
		this.title = title;
		this.version = version;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Book{" + "isbn='" + isbn + '\'' + ", title='" + title + '\'' + ", version=" + version + '}';
	}

}
