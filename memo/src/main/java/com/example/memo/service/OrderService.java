package com.example.memo.service;

import com.example.memo.entity.Cart;
import com.example.memo.entity.Order;
import com.example.memo.entity.OrderProduct;
import com.example.memo.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID must not be null.");
        }


        Cart cart = cartService.getCart(customerId);
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for customer with ID: " + customerId);
        }


        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setPrice(cart.getPrice());
        List<OrderProduct> orderProducts = new ArrayList<>();

        for (OrderProduct cartProduct : cart.getOrderProducts()) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setName(cartProduct.getName());
            orderProduct.setPrice(cartProduct.getPrice());
            orderProduct.setQuantity(cartProduct.getQuantity());
            orderProduct.setOrder(order); // Bağlantı kurduk
            orderProducts.add(orderProduct);
        }

        order.setProducts(orderProducts);

        orderRepository.save(order);

        cartService.emptyCart(customerId);

        return order;
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID must not be null.");
        }

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        if (orders.isEmpty()) {
            throw new NoSuchElementException("No orders found for customer with ID: " + customerId);
        }

        return orders;
    }
    public Order getOrderById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID must not be null.");
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
    }

}
