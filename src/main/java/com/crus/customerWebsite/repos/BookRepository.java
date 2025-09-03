package com.crus.customerWebsite.repos;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByCustomerId(Long id);

    List<Book> findByCustomer(Customer customer);
}
