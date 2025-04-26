package com.example.memo.controller;

import com.example.memo.entity.Product;
import com.example.memo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable String name) {
        try {
            Product product = productService.getProductByName(name);
            return ResponseEntity.ok(product);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product productDto) {
        try {
            productService.createProduct(productDto);
            return ResponseEntity.ok("The product has been saved.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid product data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<String> updateProduct(@RequestBody Product productDto) {
        try {
            productService.updateProduct(productDto);
            return ResponseEntity.ok("The product has been updated.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid product data: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("The product does not exist: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Product not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}
