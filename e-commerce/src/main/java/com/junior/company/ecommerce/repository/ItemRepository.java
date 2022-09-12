package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
