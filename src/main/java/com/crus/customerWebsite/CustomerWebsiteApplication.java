package com.crus.customerWebsite;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.models.Role;
import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.repos.RoleRepository;
import com.crus.customerWebsite.repos.UserRepository;
import com.crus.customerWebsite.services.BookService;
import com.crus.customerWebsite.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class CustomerWebsiteApplication implements CommandLineRunner {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private BookService bookService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(CustomerWebsiteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

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

        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByRole(Role.Roles.ROLE_USER);
            if (userRole == null) {
                userRole = Role.builder()
                    .role(Role.Roles.ROLE_USER)
                    .build();
                userRole = roleRepository.saveAndFlush(userRole);
            }
            Role adminRole = roleRepository.findByRole(Role.Roles.ROLE_ADMIN);
            if (adminRole == null) {
                adminRole = Role.builder()
                    .role(Role.Roles.ROLE_ADMIN)
                    .build();
                adminRole = roleRepository.saveAndFlush(adminRole);
            }

            Customer adminCustomer = Customer.builder()
                    .fullName("Admin")
                    .emailAddress("admin@gmail.com")
                    .address("Admin Address")
                    .age(30)
                    .build();
            Customer savedAdminCustomer = customerService.saveCustomer(adminCustomer);

            savedAdminCustomer = customerService.findById(savedAdminCustomer.getId());


            User admin = User.builder()
                    .username("admin")
                    .password(encoder.encode("admin"))
                    .authorities(Collections.singletonList(adminRole))
                    .customer(savedAdminCustomer)
                    .build();
            User savedAdmin = userRepository.save(admin);

            Role managedAdminRole = roleRepository.findByRole(Role.Roles.ROLE_ADMIN);
            savedAdmin.setAuthorities(Collections.singletonList(managedAdminRole));
            userRepository.save(savedAdmin);

            System.out.println("Admin created");
        }
	}
}
