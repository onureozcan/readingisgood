package com.example.readingisgood.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.readingisgood.dto.eventbus.EventPayloadHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    @Value("${sqs.endpoint}")
    private String sqsEndpoint;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AmazonSQS amazonSQS;

    public void produce(String queueName, String key, String value) throws JsonProcessingException {
        amazonSQS.sendMessage(
                new SendMessageRequest()
                        .withQueueUrl(sqsEndpoint + "/queue/" + queueName)
                        .withMessageGroupId(key)
                        .withMessageBody(
                                objectMapper.writeValueAsString(
                                        new EventPayloadHolder(
                                                queueName, key, value
                                        )
                                )
                        )
        );
    }
}
