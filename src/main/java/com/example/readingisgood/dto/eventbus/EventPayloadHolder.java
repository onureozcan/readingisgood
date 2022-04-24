package com.example.readingisgood.dto.eventbus;

public class EventPayloadHolder {

    private String queueName;
    private String key;
    private String payload;

    public EventPayloadHolder(String queueName, String key, String payload) {
        this.queueName = queueName;
        this.key = key;
        this.payload = payload;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
