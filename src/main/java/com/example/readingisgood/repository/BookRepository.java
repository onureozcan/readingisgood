package com.example.readingisgood.repository;

import com.example.readingisgood.model.Book;
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
import java.util.Optional;

@Repository
public class BookRepository {

    private static final String COLLECTION = "book";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<Book> findByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));

        Optional<Document> documentOptional = mongoTemplate.find(query, Document.class, COLLECTION)
                .stream().findFirst();

        if (documentOptional.isEmpty()) {
            return Optional.empty();
        }

        Document document = documentOptional.get();
        document.put("id", document.get("_id"));

        try {
            return Optional.of(objectMapper.readValue(document.toJson(), Book.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Book save(Book book) {
        Instant updatedAt = Instant.now();
        book.setUpdatedAt(updatedAt);
        book.setCreatedAt(book.getCreatedAt() == null ? updatedAt : book.getCreatedAt());

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(book.getId()));

        Update update = new Update();
        objectMapper.valueToTree(book).fields()
                .forEachRemaining(e -> {
                    String key = e.getKey();
                    if (e.getValue().isNumber()){
                        update.set(key, e.getValue().numberValue());
                    } else {
                        update.set(key, e.getValue().textValue());
                    }
                });

        update.set("_id", book.getId());
        mongoTemplate.upsert(query, update, Book.class, COLLECTION);

        return book;
    }
}
