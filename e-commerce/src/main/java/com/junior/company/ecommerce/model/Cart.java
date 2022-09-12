package com.junior.company.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name="cart")
@Getter
@SuperBuilder
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_value")
    private Double totalValue;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> cartItems;

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }
}
