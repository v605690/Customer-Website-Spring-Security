package com.crus.customerWebsite.controllers;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.models.User;
import com.crus.customerWebsite.services.BookService;
import com.crus.customerWebsite.services.CustomerService;
import com.crus.customerWebsite.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;
    private final BookService bookService;

    @GetMapping("/customer-view")
    public String showCustomerViewPage(Model model, Authentication authentication) {
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Customer customer = user.getCustomer();

        model.addAttribute("user", user);
        model.addAttribute("customer", customer);

        return "customer-view";
    }

    @GetMapping("/")
    public String ViewHomePage(Model model) {

        return "index";
    }

    @GetMapping("/customer-list")
    public String showAdminPage(Model model) {
        final List<Customer> customerList = customerService.getAllCustomers();
        model.addAttribute("customerList", customerList);

        return "customer-list";
    }

    @GetMapping("/new")
    public String showNewCustomerPage(Model model) {

        // a new (empty) Customer is created and added to the model
        Customer customer = new Customer();
        model.addAttribute("customer", customer);

        // return the "new-customer" view
        return "new-customer";
    }

    @PostMapping(value = "/save")
    // As the Model is received back from the view, @ModelAttribute
    // creates a Customer based on the object you collected from
    // the HTML page above

    public String saveCustomer(@ModelAttribute("customer") Customer customer, Model model) {

        if (customer == null) {
            model.addAttribute("message", "Customer cannot be null");

            return "error";
        }
        if (customer.getFullName() == null || customer.getFullName().isEmpty()) {
            model.addAttribute("message", "Customer name cannot be empty");

            return "error";
        }

        customerService.saveCustomer(customer);
        return "redirect:/customer-list";
    }

    @GetMapping("/edit/{id}")
    // the path variable "id" is used to pull a customer from the databas
    public ModelAndView showEditCustomerPage(@PathVariable(name = "id") Long id) {

        Customer customer = customerService.getCustomer(id);

        if (customer == null) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("Customer with ID " + id + " not found");
            return mav;
        }
        if (!id.equals(customer.getId())) {
            ModelAndView mav2 = new ModelAndView("error");
            mav2.addObject("ID mismatch: URL ID " + id +
                    " does not match customer ID " + customer.getId());
            return mav2;
        }

        // since the previous methods use Model, this one uses ModelAndView
        // to get some experience using both. Model is more common these days,
        // but ModelAndView accomplishes the same thing and can be useful in
        // certain circumstances. The view name is passed to the constructor.

        ModelAndView mav = new ModelAndView("edit-customer");
        mav.addObject("customer", customer);
        return mav;
    }

//    @GetMapping("/edit/{id}")
//    public String showEditCustomerPage(@PathVariable(name = "id") Long id, Model model) {
//        Customer customer = customerService.getCustomer(id);
//
//        if (customer == null) {
//            // Add a message to the model for the error page
//            model.addAttribute("message", "Customer with ID " + id + " not found");
//            return "error";
//        }
//        if (!id.equals(customer.getId())) {
//            // Add a message to the model for the error page
//            model.addAttribute("message", "ID mismatch: URL ID " + id +
//                    " does not match customer ID " + customer.getId());
//            return "error";
//        }
//
//        model.addAttribute("customer", customer);
//        return "redirect:/customer-list";
//    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable(name = "id") Long id,
                                 @ModelAttribute("customer") Customer customer, Model  model) {

        if (!id.equals(customer.getId())) {
            model.addAttribute("message",
                    "Cannot update, customer id " + customer.getId()
            + " doesn't match id to update: " + id + ".");
            return "error";
        }

        customerService.saveCustomer((customer));
        return "redirect:/customer-list";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Long id) {

        customerService.deleteCustomer(id);
        return "redirect:/customer-list";
    }
}
