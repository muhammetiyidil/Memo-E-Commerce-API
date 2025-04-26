package com.example.memo.repository;

import com.example.memo.entity.OrderProduct;
import com.example.memo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    OrderProduct findByName(String productName);
}
