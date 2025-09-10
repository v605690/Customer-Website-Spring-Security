package com.crus.customerWebsite.services;

import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.repos.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    // Constructor injection thanks to @RequiredArgsConstructor
    private final CustomerRepository customerRepository;

    // The findAll function gets all the customers
    // by doing a SELECT query in the DB.
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // The save function uses an INSERT query in the DB.
    @Override
    @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // The findById function uses a SELECT query
    // with a WHERE clause in the DB.
    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // The deleteById function deletes the customer
    // by doing a DELETE in the DB.
    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // The saveAll function would do multiple INSERTS into the DB.
    @Override
    @Transactional
    public List<Customer> saveAllCustomer(List<Customer> customerList) {
        try {
        return customerRepository.saveAll(customerList);
        } catch (Exception e) {
            log.error("Failed to save customers: {}", e.getMessage());
            throw new RuntimeException("Could not save customers", e);
        }
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer findbyEmailAddress(String username) {
        return customerRepository.findByEmailAddress(username);
    }

    @Override
    public Customer findByAge(Integer age) {
        return customerRepository.findByAge(age);
    }

    @Override
    public Customer findByAddress(String address) {
        return customerRepository.findByAddress(address);
    }

    @Override
    public Customer findByFullName(String fullName) {
        return customerRepository.findByFullName(fullName);
    }
}
