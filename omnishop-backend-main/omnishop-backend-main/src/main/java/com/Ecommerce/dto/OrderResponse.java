package com.Ecommerce.dto;


import java.time.LocalDate;
import java.util.List;

import com.Ecommerce.Entity.OrderStatus;

public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private Double totalAmount;
    private String Address;
    private Long userId;
    private List<OrderItemResponse> orderItem;

    public List<OrderItemResponse> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemResponse> orderItem) {
        this.orderItem = orderItem;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "OrderResponse [orderId=" + orderId + ", status=" + status + ", orderDate=" + orderDate
                + ", deliveryDate=" + deliveryDate + ", totalAmount=" + totalAmount + "]";
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
