package com.codecademy.coffeeorders.repository;

import com.codecademy.coffeeorders.model.CoffeeOrder;
import org.springframework.data.repository.CrudRepository;

public interface CoffeeOrderRepository extends CrudRepository<CoffeeOrder, Long> {
}
