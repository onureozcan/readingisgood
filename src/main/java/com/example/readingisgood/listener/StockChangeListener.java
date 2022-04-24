package com.example.readingisgood.listener;

import com.example.readingisgood.dto.eventbus.EventPayloadHolder;
import com.example.readingisgood.dto.request.StockUpdateRequest;
import com.example.readingisgood.exception.NegativeStockException;
import com.example.readingisgood.service.book.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(StockChangeListener.class.getName());

    @JmsListeners(
            {
                    @JmsListener(destination = "${queue.stock-updated}"),
                    @JmsListener(destination = "${queue.order-placed}")
            }
    )
    public void listen(TextMessage message) throws JMSException, JsonProcessingException {
        EventPayloadHolder payloadHolder = objectMapper.readValue(message.getText(), EventPayloadHolder.class);
        if (payloadHolder.getQueueName().equals(stockUpdatedQueueName)) {
            try {
                bookService.handleStockUpdate(
                        objectMapper.readValue(payloadHolder.getPayload(), StockUpdateRequest.class)
                );
            } catch (NegativeStockException exception) {
                log.error("Stock update request denied!", exception);
            }
        }
        message.acknowledge();
    }
}
