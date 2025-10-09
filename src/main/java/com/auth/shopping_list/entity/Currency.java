package com.auth.shopping_list.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "currency")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name = "price", precision=19, scale=2)
    private BigDecimal price = BigDecimal.ZERO;
    @NotNull
    @Column(name = "qty", nullable = false, precision=19, scale=2)
    private BigDecimal qty = BigDecimal.ONE;
    @Column(name = "total_price", precision=19, scale=2)
    private BigDecimal totalPrice = BigDecimal.ZERO;
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        if (price == null) price = BigDecimal.ZERO;
        if (qty == null) qty = BigDecimal.ONE;
        this.totalPrice = price.multiply(qty);
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price : BigDecimal.ZERO;
        calculateTotalPrice();
    }
    public void setQty(BigDecimal qty) {
        this.qty = qty != null ? qty : BigDecimal.ONE;
        calculateTotalPrice();
    }
}
