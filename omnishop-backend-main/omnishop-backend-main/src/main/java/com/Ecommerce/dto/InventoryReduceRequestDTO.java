package com.Ecommerce.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryReduceRequestDTO {

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Long productId;

    // ✅ Parameterized constructor
    public InventoryReduceRequestDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // getters & setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
