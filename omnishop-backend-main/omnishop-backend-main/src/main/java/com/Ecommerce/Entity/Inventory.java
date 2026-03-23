package com.Ecommerce.Entity;



import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 5;

    private Boolean active;
    
    @jakarta.persistence.Column(columnDefinition = "vector(384)")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.VECTOR)
    private float[] embedding; // String ki jagah float[] use karo

    public float[] getEmbedding() { return embedding; }
    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    public Inventory() {
    }

    public Inventory(Long productId, String productName, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        updateStatus();
    }


    public void updateStatus() {

        if (quantity == null || quantity <= 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } 
        else if (reorderLevel != null && quantity <= reorderLevel) {
            this.status = InventoryStatus.LOW_STOCK;
        } 
        else {
            this.status = InventoryStatus.IN_STOCK;
        }
    }

    // -------- Getters & Setters --------

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateStatus();
    }

    public InventoryStatus getStatus() {
        return status;
    }


    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
        updateStatus();
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}