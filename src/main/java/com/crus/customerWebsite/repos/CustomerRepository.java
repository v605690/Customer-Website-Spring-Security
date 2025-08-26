package com.crus.customerWebsite.repos;

import com.crus.customerWebsite.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
