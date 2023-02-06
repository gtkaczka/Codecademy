package com.codecademy.coffeeorders.controller;

import com.codecademy.coffeeorders.model.CoffeeOrder;
import com.codecademy.coffeeorders.repository.CoffeeOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/orders")
@RestController
public class CoffeeOrdersController {
    private final CoffeeOrderRepository coffeeOrderRepository;

    public CoffeeOrdersController(CoffeeOrderRepository coffeeOrderRepository) {
        this.coffeeOrderRepository = coffeeOrderRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoffeeOrder addCoffeeOrder(@RequestBody CoffeeOrder coffeeOrder) {
        if (ObjectUtils.isEmpty(coffeeOrder.getCustomer()) || ObjectUtils.isEmpty(coffeeOrder.getBlend())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        coffeeOrder.setFulfilled(false);
        return coffeeOrderRepository.save(coffeeOrder);
    }

    @GetMapping("/{id}")
    public CoffeeOrder getCoffeeOrder(@PathVariable Long id) {
        Optional<CoffeeOrder> coffeeOrder = coffeeOrderRepository.findById(id);
        if (coffeeOrder.isPresent()) {
            return coffeeOrder.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}")
    public CoffeeOrder fulfillCoffeeOrder(@PathVariable Long id) {
        Optional<CoffeeOrder> optionalCoffeeOrder = coffeeOrderRepository.findById(id);
        if (!optionalCoffeeOrder.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        CoffeeOrder coffeeOrder = optionalCoffeeOrder.get();
        coffeeOrder.setFulfilled(true);
        return coffeeOrderRepository.save(coffeeOrder);
    }

    @GetMapping
    public Iterable<CoffeeOrder> getAllOrders() {
        return coffeeOrderRepository.findAll();
    }
}
