package com.example.readingisgood.controller;

import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.service.order.OrderService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation("Places an order to process later")
    public Order place(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    @ApiOperation("Find an order by id")
    public Order getById(@RequestParam String id) {
        return orderService.getById(id);
    }

    @GetMapping("/list-all")
    @ApiOperation("List all the orders in a date range. Managers only")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Order> listAll(@RequestParam Instant from, @RequestParam Instant to) {
        return orderService.listOrdersOfCurrentUser(from, to);
    }

    @GetMapping("/list")
    @ApiOperation("List all your orders in a date range")
    public List<Order> list(@RequestParam Instant from, @RequestParam Instant to) {
        return orderService.list(from, to);
    }
}
