package com.auth.shopping_list.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "currency_rates",
        uniqueConstraints = @UniqueConstraint(name = "uq_currency_name", columnNames = "name"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @Column(precision = 19, scale = 6, nullable = false)
    private BigDecimal rate;

    @Column(nullable = false)
    private String date;

    @Version
    private Long version;

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "name='" + name + '\'' +
                ", rate=" + rate +
                ", date='" + date + '\'' +
                '}';
    }

    public CurrencyRate(String name, BigDecimal rate, String date) {
        this.name = name;
        this.rate = rate;
        this.date = date;
    }
}
