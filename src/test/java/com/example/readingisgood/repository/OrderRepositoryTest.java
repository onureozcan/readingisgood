package com.example.readingisgood.repository;

import com.example.readingisgood.TestMongoDb;
import com.example.readingisgood.dto.response.OrderAggregationResult;
import com.example.readingisgood.enums.OrderStatus;
import com.example.readingisgood.model.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class OrderRepositoryTest {

    public static final Instant NOW = Instant.parse("2021-06-05T00:00:00.000Z");

    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        TestMongoDb.start();
    }

    @AfterAll
    public static void afterAll() {
        TestMongoDb.stop();
    }

    @BeforeEach
    public void before() {
        orderRepository.deleteAll();
    }

    @Test
    public void testCreateNewOrder() {
        Order order = getTestOrder(null, null);
        order = orderRepository.save(order);

        Order found = orderRepository.findOrderById(order.getId()).orElseThrow();
        assertOrder(order, found);
    }

    @Test
    public void testMonthlyStats() {
        Instant from = NOW.minus(Duration.ofDays(70));
        Instant to = NOW.minus(Duration.ofDays(5));

        int totalOrderCount = 10;
        for (int i = 0; i < totalOrderCount; i++) {
            orderRepository.save(getTestOrder(from.plus(Duration.ofDays(i * 5)), "a"));
        }

        List<OrderAggregationResult> result = orderRepository.getOrderStats(from, to);
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(totalOrderCount * 2, result.stream().map(OrderAggregationResult::getCount)
                        .reduce(Integer::sum).orElseThrow()),
                () -> assertEquals(totalOrderCount * 10.0, result.stream().map(OrderAggregationResult::getTotalAmount)
                        .reduce(Double::sum).orElseThrow())
        );
    }

    @Test
    public void testFindOrdersInRangeByUserId() {
        Instant from = NOW.minus(Duration.ofDays(10));
        Instant to = NOW.minus(Duration.ofDays(5));

        int expectedOrderCount = 5;
        int totalOrderCount = 10;
        for (int i = 0; i < totalOrderCount; i++) {
            orderRepository.save(getTestOrder(from.plus(Duration.ofDays(i - (totalOrderCount - expectedOrderCount))), "a"));
        }

        for (int i = 0; i < expectedOrderCount; i++) {
            orderRepository.save(getTestOrder(from.plus(Duration.ofDays(i)), "abc"));
        }

        List<Order> foundOrders = orderRepository.findOrders("a", from, to);
        assertAll(
                () -> assertEquals(expectedOrderCount, foundOrders.size()),
                () -> assertTrue(foundOrders.stream()
                        .allMatch(it -> it.getCreatedAt().isAfter(from.minusMillis(1))
                                && it.getCreatedAt().isBefore(to) && it.getUserId().equals("a")))
        );
    }

    @Test
    public void testFindOrdersInRange() {
        Instant from = NOW.minus(Duration.ofDays(10));
        Instant to = NOW.minus(Duration.ofDays(5));

        int expectedOrderCount = 5;
        int totalOrderCount = 10;
        for (int i = 0; i < totalOrderCount; i++) {
            orderRepository.save(getTestOrder(from.plus(Duration.ofDays(i - (totalOrderCount - expectedOrderCount))), null));
        }

        List<Order> foundOrders = orderRepository.findOrders(from, to);
        assertAll(
                () -> assertEquals(expectedOrderCount, foundOrders.size()),
                () -> assertTrue(foundOrders.stream()
                        .allMatch(it -> it.getCreatedAt().isAfter(from.minusMillis(1)) && it.getCreatedAt().isBefore(to)))
        );
    }

    private Order getTestOrder(@Nullable Instant createdAt, @Nullable String userId) {
        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);
        order.setCount(2);
        order.setBookId("book_1");
        order.setTotalPaid(10.0);
        order.setUserId(userId == null ? "user@user.com" : userId);
        order.setCreatedAt(createdAt);
        return order;
    }

    private void assertOrder(Order order, Order found) {
        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(order.getId(), found.getId()),
                () -> assertEquals(order.getBookId(), found.getBookId()),
                () -> assertEquals(order.getStatus(), found.getStatus()),
                () -> assertEquals(order.getUserId(), found.getUserId()),
                () -> assertEquals(order.getCount(), found.getCount())
        );
    }
}
