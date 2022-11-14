package com.junior.company.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "item")
@Getter
@NoArgsConstructor
@SuperBuilder
public class Item {

    public Item(String size, Integer quantity) {
        this.size = size;
        this.quantity = quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "size")
    private String size;

    @Column(name = "quantity")
    @Setter
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Setter
    private Product product;
}
