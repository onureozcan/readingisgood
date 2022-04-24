package com.example.readingisgood;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.Collection;
import java.util.Collections;

@Configuration
@Profile("test")
public class MongoTestConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "readingisgood";
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new
                ConnectionString("mongodb://localhost:27017");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.example.readingisgood");
    }
}

