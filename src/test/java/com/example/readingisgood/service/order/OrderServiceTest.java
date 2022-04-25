package com.example.readingisgood.service.order;

import com.example.readingisgood.dto.eventbus.OrderProcessRequest;
import com.example.readingisgood.dto.request.CreateOrderRequest;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.enums.OrderStatus;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.exception.NoSuchBookException;
import com.example.readingisgood.exception.OrderCountNegativeException;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.pojo.auth.CustomAuthentication;
import com.example.readingisgood.repository.OrderRepository;
import com.example.readingisgood.service.MessageProducer;
import com.example.readingisgood.service.auth.AuthenticationService;
import com.example.readingisgood.service.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

    private static final Book BOOK = new Book();

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private MessageProducer messageProducer;

    @BeforeEach
    public void before() {
        BOOK.setId("book_1");
        BOOK.setPrice(12.5);

        when(orderRepository.findOrderById(any())).thenReturn(
                Optional.empty()
        );

        when(authenticationService.getCurrentUser()).thenReturn(
                new CustomAuthentication("test@test.com", "test", "test@test.com", List.of())
        );

        when(bookService.getBookById(BOOK.getId())).thenReturn(BOOK);
    }

    @Test
    public void shouldPlaceOrder() {
        orderService.createOrder(
                new CreateOrderRequest(BOOK.getId(), 2)
        );

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());

        assertAll(
                () -> assertEquals("test@test.com", orderCaptor.getValue().getUserId()),
                () -> assertEquals(BOOK.getId(), orderCaptor.getValue().getBookId()),
                () -> assertEquals(BOOK.getPrice() * 2, orderCaptor.getValue().getTotalPaid()),
                () -> assertEquals(OrderStatus.PLACED, orderCaptor.getValue().getStatus())
        );
    }

    @Test
    public void shouldFailToPlaceOrderWithNegativeCount() {
        assertThrows(OrderCountNegativeException.class, () -> orderService.createOrder(
                new CreateOrderRequest(BOOK.getId(), -2)
        ));
    }

    @Test
    public void shouldFailToPlaceOrderWithNonExistentBook() {
        when(bookService.getBookById(BOOK.getId())).thenThrow(
                new NoSuchBookException(BOOK.getId())
        );

        assertThrows(NoSuchBookException.class, () -> orderService.createOrder(
                new CreateOrderRequest(BOOK.getId(), 2)
        ));
    }

    @Test
    public void shouldHandleOrder() throws NegativeStockException {
        Order testOrder = getTestOrder();
        when(orderRepository.findOrderById(eq("order1"))).thenReturn(
                Optional.of(testOrder)
        );

        orderService.handle(new OrderProcessRequest("order1"));

        ArgumentCaptor<StockUpdateRequest> stockUpdateRequestArgumentCaptor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(bookService, times(1)).handleStockUpdate(
                stockUpdateRequestArgumentCaptor.capture()
        );

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());

        assertAll(
                () -> assertEquals("oder1", orderArgumentCaptor.getValue().getId()),
                () -> assertEquals(OrderStatus.ACCEPTED, orderArgumentCaptor.getValue().getStatus()),
                () -> assertEquals(BOOK.getId(), stockUpdateRequestArgumentCaptor.getValue().getIsbn())
        );
    }

    @Test
    public void shouldRejectOrderIfOutOfStock() throws NegativeStockException {
        Order testOrder = getTestOrder();
        when(orderRepository.findOrderById(eq("order1"))).thenReturn(
                Optional.of(testOrder)
        );

        doThrow(NegativeStockException.class).when(bookService).handleStockUpdate(any());

        orderService.handle(new OrderProcessRequest("order1"));

        ArgumentCaptor<StockUpdateRequest> stockUpdateRequestArgumentCaptor = ArgumentCaptor.forClass(StockUpdateRequest.class);
        verify(bookService, times(1)).handleStockUpdate(
                stockUpdateRequestArgumentCaptor.capture()
        );

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());

        assertAll(
                () -> assertEquals("oder1", orderArgumentCaptor.getValue().getId()),
                () -> assertEquals(OrderStatus.REJECTED, orderArgumentCaptor.getValue().getStatus()),
                () -> assertEquals(BOOK.getId(), stockUpdateRequestArgumentCaptor.getValue().getIsbn())
        );
    }

    private Order getTestOrder() {
        Order order = new Order();
        order.setId("oder1");
        order.setStatus(OrderStatus.PLACED);
        order.setCount(2);
        order.setBookId(BOOK.getId());
        order.setBook(BOOK);
        order.setTotalPaid(10.0);
        order.setUserId("user@user.com");
        return order;
    }
}
