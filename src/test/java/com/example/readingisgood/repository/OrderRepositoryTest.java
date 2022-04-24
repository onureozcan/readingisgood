package com.example.readingisgood.repository;

import com.example.readingisgood.TestMongoDb;
import com.example.readingisgood.enums.OrderStatus;
import com.example.readingisgood.model.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class OrderRepositoryTest {

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

    @Test
    public void testCreateNewOrder() {
        Order order = getTestOrder();
        order = orderRepository.save(order);

        Order found = orderRepository.findOrderById(order.getId()).orElseThrow();
        assertOrder(order, found);
    }

    private Order getTestOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);
        order.setCount(3);
        order.setBookId("book_1");
        order.setUserId("user@user.com");
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
