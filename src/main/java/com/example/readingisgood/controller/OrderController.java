package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/private/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order place(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public Order getById(@RequestParam String id) {
        return orderService.getById(id);
    }

    @GetMapping("/list-all")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Order> listAll(@RequestParam Instant from, @RequestParam Instant to) {
        return orderService.list(from, to);
    }

    @GetMapping("/list")
    public List<Order> list(@RequestParam Instant from, @RequestParam Instant to) {
        return orderService.list(from, to);
    }
}
