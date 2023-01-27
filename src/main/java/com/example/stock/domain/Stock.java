package com.example.stock.domain;

import jakarta.persistence.*;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    public Stock() {
    }

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // 수량체크를 위해 수량 getter 생성
    public Long getQuantity() {
        return quantity;
    }

    // 수량감소 로직
    public void decrease(Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new RuntimeException("재고가 - 입니다.");
        }
        this.quantity = this.quantity - quantity;
    }
}
