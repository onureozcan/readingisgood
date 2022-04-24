package com.example.readingisgood.repository;

import com.example.readingisgood.model.Book;
import com.example.readingisgood.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepository {

    private static final String COLLECTION = "order";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM").withZone(ZoneId.systemDefault());

    public Optional<Order> findOrderById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Optional<Document> documentOptional = mongoTemplate.find(query, Document.class, COLLECTION)
                .stream().findFirst();

        if (documentOptional.isEmpty()) {
            return Optional.empty();
        }

        Document document = documentOptional.get();
        document.put("id", document.get("_id"));

        try {
            return Optional.of(objectMapper.readValue(document.toJson(), Order.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Order save(Order order) {
        Instant updatedAt = Instant.now();
        order.setUpdatedAt(updatedAt);
        order.setCreatedAt(order.getCreatedAt() == null ? updatedAt : order.getCreatedAt());

        String id = order.getId();
        if (id == null) {
            id = generateId(order);
            order.setId(id);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(order.getId()));

        Update update = new Update();
        objectMapper.valueToTree(order).fields()
                .forEachRemaining(e -> {
                    String key = e.getKey();
                    if (e.getValue().isNumber()) {
                        update.set(key, e.getValue().numberValue());
                    } else {
                        update.set(key, e.getValue().textValue());
                    }
                });

        update.set("_id", order.getId());
        mongoTemplate.upsert(query, update, Order.class, COLLECTION);
        return order;
    }

    private String generateId(Order order) {
        // so that a range query over the id field can be utilised
        Instant date = order.getCreatedAt();
        return formatter.format(date) + "#" + order.getUserId() + "#" + UUID.randomUUID();
    }
}
