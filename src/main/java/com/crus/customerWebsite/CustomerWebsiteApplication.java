package com.crus.customerWebsite;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.services.BookService;
import com.crus.customerWebsite.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class CustomerWebsiteApplication implements CommandLineRunner {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private BookService bookService;

	public static void main(String[] args) {
		SpringApplication.run(CustomerWebsiteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		customerService.saveAllCustomer(Arrays.asList(
				Customer.builder()
						.fullName("Customer 1")
						.emailAddress("customer1@gmail.com")
						.address("Customer Address One")
						.age(30)
						.build(),
				Customer.builder()
						.fullName("Customer 2")
						.emailAddress("customer2@gmail.com")
						.address("Customer Address Two")
						.age(28)
						.build(),
				Customer.builder()
						.fullName("Customer 3")
						.emailAddress("customer3@gmail.com")
						.address("Customer Address Three")
						.age(33)
						.build()
		));

		bookService.saveAllBooks(Arrays.asList(
                Book.builder()
                        .title("Title 1")
                        .author("Author 1")
                        .isbn("978-0-439-02348-1")
						.build(),
				Book.builder()
						.title("Title 2")
						.author("Author 2")
						.isbn("978-0-439-02348-2")
						.build(),
				Book.builder()
						.title("Title 3")
						.author("Author 1")
						.isbn("978-0-439-02348-3")
						.build()
        ));
	}
}
