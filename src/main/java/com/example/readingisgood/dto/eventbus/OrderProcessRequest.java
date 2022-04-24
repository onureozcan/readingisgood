package com.example.readingisgood.dto.eventbus;

public class OrderProcessRequest {

    private String orderId;

    public OrderProcessRequest(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
