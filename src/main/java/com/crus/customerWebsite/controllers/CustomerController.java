package com.crus.customerWebsite.controllers;

import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/")
    public String ViewHomePage(Model model) {
        final List<Customer> customerList = customerService.getAllCustomers();

        model.addAttribute("customerList", customerList);

        return "index";
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
        return "redirect:/";
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
        return "redirect:/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Long id) {

        customerService.deleteCustomer(id);
        return "redirect:/";
    }
}
