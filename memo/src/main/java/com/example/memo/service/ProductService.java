package com.example.memo.service;

import com.example.memo.entity.Product;
import com.example.memo.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Product not found with name: " + name));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ResponseEntity<String> createProduct(Product productDto) {
        if (productDto == null || productDto.getName() == null || productDto.getPrice() == null || productDto.getQuantity() == null) {
            return ResponseEntity.badRequest().body("Invalid product data.");
        }
        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        productRepository.save(product);
        return ResponseEntity.ok("The product has been saved.");
    }

    public ResponseEntity<String> updateProduct(Product productDto) {
        if (productDto == null || productDto.getName() == null || productDto.getPrice() == null
                || productDto.getQuantity() == null || productDto.getId() == null) {
            return ResponseEntity.badRequest().body("Invalid product data.");
        }

        Optional<Product> optionalProduct = productRepository.findById(productDto.getId());
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The product does not exist.");
        }

        Product product = optionalProduct.get();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        productRepository.save(product);
        return ResponseEntity.ok("The product has been updated.");
    }

    public ResponseEntity<String> deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
