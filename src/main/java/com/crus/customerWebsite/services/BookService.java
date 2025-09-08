package com.crus.customerWebsite.services;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.repos.BookRepository;
import com.crus.customerWebsite.repos.CustomerRepository;
import com.crus.customerWebsite.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;

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

    public List<Book> findBookByCustomerId(Long id) {
        return Collections.singletonList(bookRepository.findByCustomerId(id));
    }

        // Return a book list assigned by a username
    public List<Book> getAssignedBooksByUsername(String username) {
        // getting a user
        User user = userRepository.findByUsername(username);
        // getting the customer associated with this user
        Customer customer = user.getCustomer();
        // return a book list associated with this customer
        return bookRepository.findByCustomer(customer);
    }
}