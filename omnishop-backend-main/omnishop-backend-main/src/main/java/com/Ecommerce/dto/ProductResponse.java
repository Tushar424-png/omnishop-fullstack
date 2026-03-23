package com.Ecommerce.dto;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id; // productId

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;

    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "ProductResponse [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
                + ", category=" + category + ", imageUrl=" + imageUrl + ", active="
                + active + "]";
    }

    public ProductResponse() {
        super();
        // TODO Auto-generated constructor stub
    }

}
