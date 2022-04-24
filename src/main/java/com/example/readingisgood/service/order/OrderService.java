package com.example.readingisgood.service.order;

import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.enums.OrderStatus;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.repository.OrderRepository;
import com.example.readingisgood.service.auth.AuthenticationService;
import com.example.readingisgood.service.book.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookService bookService;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class.getName());

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setBookId(request.getBookIsbn());
        order.setUserId(authenticationService.getCurrentUser().getPrincipal().toString());
        order.setCount(request.getCount());
        order.setStatus(OrderStatus.PLACED);
        return orderRepository.save(order);
    }

    public void handleOrder(Order order) {
        try {
            bookService.handleStockUpdate(
                    new StockUpdateRequest(order.getBookId(), order.getCount())
            );
            order.setStatus(OrderStatus.ACCEPTED);
        } catch (NegativeStockException e) {
            order.setStatus(OrderStatus.REJECTED);
        }
    }
}
