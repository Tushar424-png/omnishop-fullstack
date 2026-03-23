package com.Ecommerce.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryRestockRequestDTO {

    @NotNull
    @Min(1)
    private Integer quantity;
    
    @NotNull
    private Long ProductId;

    public Integer getQuantity() {
        return quantity;
    }

    public Long getProductId() {
		return ProductId;
	}

	public void setProductId(Long productId) {
		ProductId = productId;
	}

	public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
	
	

}
