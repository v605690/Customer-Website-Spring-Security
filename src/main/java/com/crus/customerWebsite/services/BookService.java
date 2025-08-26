package com.crus.customerWebsite.services;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.repos.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return getBooks().stream().filter(b -> b.getCustomer() == null)
                .collect(Collectors.toList());
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id)
                .orElse(null);
    }

    @Transactional
    public Book saveBook(Book book) throws IllegalArgumentException {
        return bookRepository.save(book);
    }

    @Transactional
    public List<Book> saveAllBooks(List<Book> bookList) {
        return bookRepository.saveAll(bookList);
    }

    public void removeBook(Long customerId) {
        Book book = bookRepository.findByCustomerId(customerId);
        if (book != null) {
            book.setCustomer(null);
            saveBook(book);
        }
    }
}
