package com.Ecommerce.dto;


import java.util.List;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String password;
    private String address;
    // Entity use mat karo, DTO use karo
    private List<CartResponse> cartItems;
    private List<WishlistResponse> wishlistItems;

    // Getters and Setters (Standard wale)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<CartResponse> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartResponse> cartItems) {
        this.cartItems = cartItems;
    }

    public List<WishlistResponse> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<WishlistResponse> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
