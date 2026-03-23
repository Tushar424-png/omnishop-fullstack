package com.Ecommerce.dto;

public class WishlistResponse {
    private Long id;

    private Long userId;      // JWT se milega
    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public WishlistResponse() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "WishlistItem [id=" + id + ", userId=" + userId + ", productId=" + productId + "]";
    }
}