package com.Ecommerce.dto;


public class CartRequest {

    private Long productId;
    private Integer quantity;
    private Long userId;

    public CartRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "CartRequest [  productId=" + productId + ", quantity=" + quantity + ", userId=" + userId
                + "]";
    }

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}