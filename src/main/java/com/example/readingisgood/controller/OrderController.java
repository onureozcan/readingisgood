package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping
    public Order getById(String id) {
        return orderService.getById(id);
    }

    @RequestMapping("/list-all")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Order> listAll(Instant from, Instant to) {
        return orderService.list(from, to);
    }

    @RequestMapping("/list")
    public List<Order> list(Instant from, Instant to) {
        return orderService.list(from, to);
    }
}
