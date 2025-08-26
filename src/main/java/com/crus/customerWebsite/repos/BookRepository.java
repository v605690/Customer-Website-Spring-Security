package com.crus.customerWebsite.repos;

import com.crus.customerWebsite.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByCustomerId(Long id);
}
