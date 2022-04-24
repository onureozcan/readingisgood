package com.example.readingisgood.listener;

import com.example.readingisgood.dto.eventbus.EventPayloadHolder;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.service.book.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class StockChangeListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    @Value("${queue.stock-updated}")
    private String stockUpdatedQueueName;

    @JmsListeners(
            {
                    @JmsListener(destination = "${queue.stock-updated}"),
                    @JmsListener(destination = "${queue.order-placed}")
            }
    )
    public void listen(TextMessage message) throws JMSException, JsonProcessingException {
        EventPayloadHolder payloadHolder = objectMapper.readValue(message.getText(), EventPayloadHolder.class);
        if (payloadHolder.getQueueName().equals(stockUpdatedQueueName)) {
            bookService.handleStockUpdate(
                    objectMapper.readValue(payloadHolder.getPayload(), StockUpdateRequest.class)
            );
        }
        message.acknowledge();
    }
}
