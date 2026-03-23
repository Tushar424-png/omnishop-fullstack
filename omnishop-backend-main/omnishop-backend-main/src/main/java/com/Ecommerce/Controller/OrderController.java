package com.Ecommerce.Controller;


import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce.Exception.ApiResponse;
import com.Ecommerce.dto.OrderItemErrorDTO;
import com.Ecommerce.dto.OrderResponse;
import com.Ecommerce.Service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderservice;

    public OrderController(OrderService orderservice) {
        this.orderservice = orderservice;
    }

    @PostMapping("/create/{id}")
    public ApiResponse<OrderResponse> createOrder(@PathVariable Long id) {
        return orderservice.createOrder(id);
    }

    @PostMapping("/check/{id}")
    public ApiResponse<List<OrderItemErrorDTO>> checkStock(@PathVariable Long id) {
        return orderservice.checkInventory(id);
    }

    @GetMapping("/getall")
    public List<OrderResponse> getall() {
        return orderservice.get();
    }

}