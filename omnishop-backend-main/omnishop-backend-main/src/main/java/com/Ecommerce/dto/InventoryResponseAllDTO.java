package com.Ecommerce.dto;

import com.Ecommerce.Entity.InventoryStatus;

public class InventoryResponseAllDTO {
	    
	    private String productName;

	    private Integer quantity;
	    
	    private InventoryStatus status;

	    private Integer reorderLevel;
	    
	    private Boolean active;
	    
	    private Long ProductId;
	    
	    public Long getProductId() {
			return ProductId;
		}


		public void updateStatus() {
	        // If either value is null, we can't calculate a status change, so we exit safely
	        if (quantity == null || reorderLevel == null) {
	            return; 
	        }

	        if (quantity <= 0) {
	            this.status = InventoryStatus.OUT_OF_STOCK;
	        } else if (quantity <= reorderLevel) {
	            this.status = InventoryStatus.LOW_STOCK;
	        } else {
	            this.status = InventoryStatus.IN_STOCK;
	        }
	    }


	    // -------- Getters & Setters --------
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


		public void setStatus(InventoryStatus status2) {
			this.status=status2;
		}


		public void setProductId(Long productId) {
		  this.ProductId=productId;
			
		}
	}

