package com.example.readingisgood.repository;

import com.example.readingisgood.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String COLLECTION = "user";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public Optional<User> findUserById(String id) {
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
            return Optional.of(objectMapper.readValue(document.toJson(), User.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Upsert a user
     *
     * @param user user to be saved. note that it will be modified
     * @return updated user entity
     */
    public User save(User user) {
        Instant updatedAt = Instant.now();
        user.setUpdatedAt(updatedAt);
        user.setCreatedAt(user.getCreatedAt() == null ? updatedAt : user.getCreatedAt());

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()));


        Update update = new Update();
        objectMapper.valueToTree(user).fields()
                .forEachRemaining(e -> update.set(e.getKey(), e.getValue().textValue()));

        update.set("_id", user.getId());
        mongoTemplate.upsert(query, update, User.class, COLLECTION);

        return user;
    }
}
