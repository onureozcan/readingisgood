package com.example.readingisgood.repository;

import com.example.readingisgood.dto.response.OrderAggregationResult;
import com.example.readingisgood.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class OrderRepository {

    private static final String COLLECTION = "order";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM").withZone(ZoneId.systemDefault());

    public void deleteAll() {
        mongoTemplate.dropCollection(COLLECTION);
    }

    public List<OrderAggregationResult> getOrderStats(Instant from, Instant to) {
        GroupOperation groupByMonth = group("yearAndMonth")
                .sum("count").as("count")
                .sum("totalPaid").as("totalAmount");

        MatchOperation matchMonths = Aggregation.match(
                Criteria.where("_id")
                        .gte(formatter.format(from))
                        .lte(formatter.format(to.plus(Duration.ofDays(30))))
        );

        SortOperation sortByDate = sort(Sort.by(Sort.Direction.DESC, "yearAndMonth"));
        Aggregation aggregation = newAggregation(
                groupByMonth, matchMonths, sortByDate);
        AggregationResults<OrderAggregationResult> result = mongoTemplate.aggregate(
                aggregation, COLLECTION, OrderAggregationResult.class);
        return result.getMappedResults();
    }

    public List<Order> findOrders(String userId, Instant from, Instant to) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("_id")
                        .gte(formatter.format(from) + "#" + userId + "#")
                        .lte(formatter.format(to.plus(Duration.ofDays(30))) + "#" + userId + "#")
                        .and("userId").is(userId) // we still need this because some user ids can include each other
        );

        return mongoTemplate.find(query, Document.class, COLLECTION)
                .stream()
                .peek(it -> it.put("id", it.get("_id")))
                .map(this::getOrder)
                .filter(it -> it.getCreatedAt().isAfter(from.minusMillis(1)) && it.getCreatedAt().isBefore(to))
                .collect(Collectors.toList());
    }

    public List<Order> findOrders(Instant from, Instant to) {
        Query query = new Query();
        query.addCriteria(
                Criteria.where("_id")
                        .gte(formatter.format(from))
                        .lte(formatter.format(to.plus(Duration.ofDays(30))))
        );

        return mongoTemplate.find(query, Document.class, COLLECTION)
                .stream()
                .peek(it -> it.put("id", it.get("_id")))
                .map(this::getOrder)
                .filter(it -> it.getCreatedAt().isAfter(from.minusMillis(1)) && it.getCreatedAt().isBefore(to))
                .collect(Collectors.toList());
    }

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

        return Optional.of(getOrder(document));
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

        // will be used to aggregate
        update.set("yearAndMonth", formatter.format(order.getCreatedAt()));

        update.set("_id", order.getId());
        mongoTemplate.upsert(query, update, Order.class, COLLECTION);
        return order;
    }

    private String generateId(Order order) {
        // so that a range query over the id field can be utilised
        Instant date = order.getCreatedAt();
        return formatter.format(date) + "#" + order.getUserId() + "#" + UUID.randomUUID();
    }

    private Order getOrder(Document it) {
        try {
            return objectMapper.readValue(it.toJson(), Order.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
