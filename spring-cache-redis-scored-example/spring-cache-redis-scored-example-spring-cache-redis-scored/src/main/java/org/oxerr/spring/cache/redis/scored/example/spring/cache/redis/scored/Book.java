package org.oxerr.spring.cache.redis.scored.example.spring.cache.redis.scored;

import java.io.Serializable;

import org.springframework.data.annotation.Version;

public class Book implements Serializable {

	private static final long serialVersionUID = 2021081401L;

	private String isbn;
	private String title;

	@Version
	private Long version;

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
