package com.Ecommerce.dto;


public class WishlistRequest {


    private Long userId;      // JWT se milega
    private Long productId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public WishlistRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "WishlistItem [userId=" + userId + ", productId=" + productId + "]";
    }
}