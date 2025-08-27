package com.crus.customerWebsite.security;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.models.Role;
import com.crus.customerWebsite.repos.BookRepository;
import com.crus.customerWebsite.repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Object targetDomainObject,
            Object permission) {
        // this method will not be used - but if used by accident,
        // should always block access for good measure.
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission) {

        if (!permission.getClass().equals("".getClass())) {
            throw new SecurityException(
                    "Cannot execute hasPermission() calls where " +
                            "permission is not in String form");
        }

        // if the user is an admin they should be allowed to proceed
        if (userIsAdmin(authentication)) {
            return true;
        } else {
            // the user must be the owner of the object to edit it.
            Customer customerDetails =
                    (Customer) authentication.getPrincipal();

            if (targetType.equalsIgnoreCase("book")) {
                Optional<Book> book =
                        bookRepository.findById(
                                Long.parseLong(targetId.toString()));
                if (book.isEmpty()) {
                    // no book with id exists, return true so the method
                    // can continue ultimately throwing an exception
                    return true;
                }

                // if the author of the entity matches the current user
                // they are the owner of the recipe and allowed access
                return book
                        .get()
                        .getAuthor()
                        .equals(customerDetails.getFullName());
            }
        }
        return true;
    }

    public boolean userIsAdmin(Authentication authentication) {
        Collection<Role> grantedAuthorities =
                (Collection<Role>) authentication.getAuthorities();

        for (Role r : grantedAuthorities) {
            if (r.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}

