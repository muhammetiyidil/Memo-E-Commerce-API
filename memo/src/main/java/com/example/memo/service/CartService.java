package com.example.memo.service;

import com.example.memo.Dto.UpdateCartRequest;
import com.example.memo.entity.Cart;
import com.example.memo.entity.OrderProduct;
import com.example.memo.entity.Product;
import com.example.memo.repository.CartRepository;
import com.example.memo.repository.OrderProductRepository;
import com.example.memo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    public Cart getCart(Long customer_id) {
        if (customer_id == null) {
            throw new IllegalArgumentException("Customer ID must not be null.");
        }

        Optional<Cart> optionalCart = cartRepository.findByCustomerId(customer_id);
        if (optionalCart.isEmpty()) {
            throw new IllegalArgumentException("Cart not found for customer with ID: " + customer_id);
        }

        return optionalCart.get();
    }

    @Transactional
    public Cart addProductToCart(Long customerId, String productName) {
        if (customerId == null || productName == null) {
            throw new IllegalArgumentException("Customer ID and Product must not be null.");
        }


        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productName));


        Cart cart = getCart(customerId);


        OrderProduct existingOrderProduct = cart.getOrderProducts().stream()
                .filter(op -> op.getName().equals(productName))
                .findFirst()
                .orElse(null);

        if (existingOrderProduct != null) {

            existingOrderProduct.setQuantity(existingOrderProduct.getQuantity() + 1);
            orderProductRepository.save(existingOrderProduct);
        } else {

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setName(product.getName());
            orderProduct.setPrice(product.getPrice());
            orderProduct.setQuantity(1);
            orderProduct.setCart(cart);

            orderProductRepository.save(orderProduct);


            if (cart.getOrderProducts() != null) {
                cart.getOrderProducts().add(orderProduct);
            } else {
                cart.setOrderProducts(new ArrayList<>(List.of(orderProduct)));
            }
        }


        updateCartPrice(cart);

        return cartRepository.save(cart);
    }

    private void updateCartPrice(Cart cart) {
        if (cart.getOrderProducts() == null || cart.getOrderProducts().isEmpty()) {
            cart.setPrice(0.0);
            return;
        }

        double totalPrice = cart.getOrderProducts().stream()
                .mapToDouble(op -> {
                    double unitPrice = op.getPrice() != null ? op.getPrice() : 0.0;
                    int quantity = op.getQuantity() != null ? op.getQuantity() : 0;
                    return unitPrice * quantity;
                })
                .sum();

        cart.setPrice(totalPrice);
    }


    @Transactional
    public Cart removeProductFromCart(Long customerId, String productName) {
        if (customerId == null || productName == null) {
            throw new IllegalArgumentException("Customer ID and productName must not be null.");
        }

        Cart cart = getCart(customerId);

        if (cart.getOrderProducts() == null || cart.getOrderProducts().isEmpty()) {
            throw new NoSuchElementException("Cart has no products.");
        }

        OrderProduct productToRemove = orderProductRepository.findByName(productName);

        cart.getOrderProducts().remove(productToRemove);

        orderProductRepository.delete(productToRemove);

        updateCartPrice(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCart(Long customerId, List<UpdateCartRequest> updateCartRequests) {
        if(customerId == null || updateCartRequests == null) {
            throw new IllegalArgumentException("Customer ID must not be null.");
        }
        Cart cart = getCart(customerId);
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for customer with ID: " + customerId);
        }

        for(UpdateCartRequest updateCartRequest : updateCartRequests) {
            String productName = updateCartRequest.getProductName();
            Integer quantity = updateCartRequest.getQuantity();

            OrderProduct orderProduct = cart.getOrderProducts().stream()
                    .filter(op -> op.getName().equals(productName))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Product not found in cart: " + productName));


            Product originalProduct = productRepository.findByName(productName)
                    .orElseThrow(() -> new NoSuchElementException("Original product not found: " + productName));

            orderProduct.setPrice(originalProduct.getPrice());
            orderProduct.setQuantity(updateCartRequest.getQuantity());
            orderProductRepository.save(orderProduct);

        }

        updateCartPrice(cart);


        return cartRepository.save(cart);
    }

    @Transactional
    public Cart emptyCart(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID must not be null.");
        }


        Cart cart = getCart(customerId);
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for customer with ID: " + customerId);
        }


        List<OrderProduct> orderProducts = cart.getOrderProducts();
        for (OrderProduct orderProduct : orderProducts) {
            if (orderProduct.getOrder() == null ) {
                orderProductRepository.delete(orderProduct);
            }
        }
        cart.setOrderProducts(new ArrayList<>());

        cart.setPrice(0.0);


        return cartRepository.save(cart);
    }
}
