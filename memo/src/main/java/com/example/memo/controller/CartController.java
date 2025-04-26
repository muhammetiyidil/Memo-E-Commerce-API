package com.example.memo.controller;

import com.example.memo.Dto.UpdateCartRequest;
import com.example.memo.entity.Cart;
import com.example.memo.entity.Product;
import com.example.memo.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @RequestMapping("/getCart/{id}")
    public ResponseEntity<?> getCart(@PathVariable Long id) {

        try {
            Cart cart = cartService.getCart(id);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping
    @RequestMapping("/addProductToCart/{customerId}&{productName}")
    public ResponseEntity<String> addProductToCart(@PathVariable Long customerId, @PathVariable String productName ) {
        try {
            cartService.addProductToCart(customerId, productName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product successfully added to the cart.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping
    @RequestMapping("/removeProductFromCart/{customerId}&{productName}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long customerId, @PathVariable String productName ) {
        try {
            cartService.removeProductFromCart(customerId, productName);
            return ResponseEntity.ok("Product successfully removed from the cart.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping
    @RequestMapping("/updateCart/{customerId}")
    public ResponseEntity<String> updateCart(@PathVariable Long customerId, @RequestBody List<UpdateCartRequest> updateCartRequests) {
        try {
            cartService.updateCart(customerId, updateCartRequests);
            return ResponseEntity.ok("Cart updated successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the cart.");
        }
    }

    @DeleteMapping("/emptyCart/{customerId}")
    public ResponseEntity<String> emptyCart(@PathVariable Long customerId) {
        try {
            cartService.emptyCart(customerId);
            return ResponseEntity.ok("Cart emptied successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while emptying the cart: " + e.getMessage());
        }
    }
}
