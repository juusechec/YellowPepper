package com.yellowpepper.challenge.repository;
import com.yellowpepper.challenge.repository.model.Book;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {
}
