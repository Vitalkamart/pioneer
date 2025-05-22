package ru.mart.pioneer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Account extends BaseEntity {

    @Column(precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(precision = 19, scale = 4)
    private BigDecimal initialDeposit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
