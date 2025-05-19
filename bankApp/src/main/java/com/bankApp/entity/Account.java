package com.bankApp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter @Setter
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 38, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "initial_deposit", precision = 38, scale = 2, nullable = false)
    private BigDecimal initialDeposit;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)   // тоже без 'table'
    private AppUser user;
}

