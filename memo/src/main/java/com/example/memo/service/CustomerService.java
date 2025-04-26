package com.example.memo.service;

import com.example.memo.entity.Cart;
import com.example.memo.entity.Customer;
import com.example.memo.repository.CartRepository;
import com.example.memo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private CartRepository cartRepository;

    public CustomerService(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    public Customer addCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name must not be null or empty.");
        }

        boolean exists = customerRepository.existsByName(customer.getName().trim());
        if (exists) {
            throw new IllegalArgumentException("A customer with the same name already exists.");
        }

        Customer savedCustomer = customerRepository.save(customer);


        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        cart.setPrice(0.0);
        cartRepository.save(cart);

        return savedCustomer;
    }

}
