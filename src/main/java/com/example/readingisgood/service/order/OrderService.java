package com.example.readingisgood.service.order;

import com.example.readingisgood.dto.eventbus.OrderProcessRequest;
import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.enums.OrderStatus;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.exception.NoSuchOrderException;
import com.example.readingisgood.exception.OrderCountNegativeException;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.repository.BookRepository;
import com.example.readingisgood.repository.OrderRepository;
import com.example.readingisgood.service.MessageProducer;
import com.example.readingisgood.service.auth.AuthenticationService;
import com.example.readingisgood.service.book.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${queue.order-placed}")
    private String orderPlacedQueueName;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class.getName());

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setBookId(request.getBookIsbn());
        order.setUserId(authenticationService.getCurrentUser().getPrincipal().toString());
        order.setCount(request.getCount());
        if (order.getCount() < 0) {
            throw new OrderCountNegativeException();
        }

        order.setStatus(OrderStatus.PLACED);
        order.setBook(bookService.getBookById(request.getBookIsbn()));
        order.setTotalPaid(request.getCount() * order.getBook().getPrice());
        orderRepository.save(order);

        try {
            messageProducer.produce(
                    orderPlacedQueueName,
                    order.getBookId(),
                    objectMapper.writeValueAsString(new OrderProcessRequest(order.getId()))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    public void handle(OrderProcessRequest request) {
        Order order = getById(request.getOrderId());
        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            return;
        }
        try {
            bookService.handleStockUpdate(
                    new StockUpdateRequest(order.getBookId(), order.getCount())
            );
            order.setStatus(OrderStatus.ACCEPTED);
        } catch (NegativeStockException e) {
            order.setStatus(OrderStatus.REJECTED);
        }
        orderRepository.save(order);
    }

    public Order getById(String id) {
        return orderRepository.findOrderById(id).orElseThrow(() -> new NoSuchOrderException(id));
    }

    public List<Order> list(Instant from, Instant to) {
        return orderRepository.findOrders(from, to);
    }

    public List<Order> listOrdersOfCurrentUser(Instant from, Instant to) {
        return orderRepository.findOrders(authenticationService.getCurrentUser().getPrincipal().toString(), from, to);
    }
}
