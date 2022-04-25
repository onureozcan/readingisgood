package com.example.readingisgood.controller;

import com.example.readingisgood.dto.response.OrderAggregationResult;
import com.example.readingisgood.repository.OrderRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/private/stats")
public class OrderStatsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    @ApiOperation("Get month by month aggregated order statistics in a date range. Managers only")
    @PreAuthorize("hasRole('MANAGER')")
    public List<OrderAggregationResult> get(
            @RequestParam Instant from, @RequestParam Instant to
    ) {
        return orderRepository.getOrderStats(from, to);
    }
}
