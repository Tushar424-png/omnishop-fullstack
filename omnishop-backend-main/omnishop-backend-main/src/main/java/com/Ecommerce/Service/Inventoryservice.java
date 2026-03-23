package com.Ecommerce.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.Ecommerce.Entity.Inventory;
import com.Ecommerce.Entity.InventoryStatus;
import com.Ecommerce.Repository.InventoryRepository;
import com.Ecommerce.Repository.ProductRepository;
import com.Ecommerce.dto.InventoryCheckRequestDTO;
import com.Ecommerce.dto.InventoryCheckResponseDTO;
import com.Ecommerce.dto.InventoryCreateRequestDTO;
import com.Ecommerce.dto.InventoryReduceRequestDTO;
import com.Ecommerce.dto.InventoryResponseAllDTO;
import com.Ecommerce.dto.InventoryResponseDTO;
import com.Ecommerce.dto.InventoryRestockRequestDTO;
import jakarta.transaction.Transactional;

@Service
public class Inventoryservice {
    private final InventoryRepository inventoryRepo;
    private final AIEmbeddingService embedservice;
    private final ProductRepository productRepository;

    public Inventoryservice(InventoryRepository inventoryRepo,AIEmbeddingService embedservice,ProductRepository productRepository) {
        this.inventoryRepo = inventoryRepo;
        this.embedservice=embedservice;
        this.productRepository=productRepository;
    }

    @Transactional
    public InventoryResponseDTO addNewProductInInventory(InventoryCreateRequestDTO request) {
        // Embedding lo
        float[] vector = embedservice.getEmbedding(request.getProductName());
        String status = (request.getQuantity() > 0) ? "IN_STOCK" : "OUT_OF_STOCK";

        // Native save call karo (CAST ke saath)
        inventoryRepo.saveWithVector(
                request.getProductId(),
                request.getProductName(),
                request.getQuantity(),
                status,
                request.getReorderLevel() != null ? request.getReorderLevel() : 5,
                request.getActive() != null ? request.getActive() : true,
                vector
        );

        Inventory saved = inventoryRepo.findByProductId(request.getProductId()).orElseThrow();
        return mapToResponse(saved);
    }
    private InventoryResponseDTO mapToResponse(Inventory inventory) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
        dto.setProductName(inventory.getProductName());
        dto.setQuantity(inventory.getQuantity());
        dto.setStatus(inventory.getStatus());
        dto.setActive(inventory.getActive());
        return dto;
    }
   
    public List<InventoryCheckResponseDTO> checkStock(
            List<InventoryCheckRequestDTO> requests) {

        List<InventoryCheckResponseDTO> responses = new ArrayList<>();

        for (InventoryCheckRequestDTO req : requests) {

            InventoryCheckResponseDTO res = new InventoryCheckResponseDTO();
            res.setProductId(req.getProductId());

            Inventory inventory = inventoryRepo
                    .findByProductIdAndActiveTrue(req.getProductId())
                    .orElse(null);

            // Product not found
            if (inventory == null) {
                res.setAvailable(false);
                res.setAvailableQuantity(0);
                res.setMessage("Product not found in inventory");
            } else {
                Integer availableQuantity = inventory.getQuantity();

                if (availableQuantity <= 0) {
                    res.setAvailable(false);
                    res.setAvailableQuantity(0);
                    res.setMessage("Out of stock");
                } else if (availableQuantity < req.getRequiredQuantity()) {
                    res.setAvailable(false);
                    res.setAvailableQuantity(availableQuantity);
                    res.setMessage("Insufficient stock");
                } else {
                    res.setAvailable(true);
                    res.setAvailableQuantity(availableQuantity);
                    res.setMessage("Stock available");
                }
            }

            responses.add(res);
        }

        return responses;
    }
    @Transactional
    public InventoryResponseDTO restockInventory(InventoryRestockRequestDTO request) {
        Inventory inventory = inventoryRepo.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        int newQty = inventory.getQuantity() + request.getQuantity();
        inventory.setQuantity(newQty);
        inventory.updateStatus();

        float[] vector = embedservice.getEmbedding(inventory.getProductName() + " " + newQty);

        // Native update call karo
        inventoryRepo.updateWithVector(
            inventory.getProductId(),
            inventory.getQuantity(),
            inventory.getStatus().name(),
            vector
        );

        return mapToResponse(inventory);
    }
    @Transactional
    public List<InventoryResponseDTO> reduceInventoryBulk(List<InventoryReduceRequestDTO> requests) {
        List<InventoryResponseDTO> responses = new ArrayList<>();

        for (InventoryReduceRequestDTO req : requests) {
            Inventory inventory = inventoryRepo.findByProductId(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int remainingQty = inventory.getQuantity() - req.getQuantity();
            if (remainingQty < 0) throw new RuntimeException("Insufficient stock");

            inventory.setQuantity(remainingQty);
            inventory.updateStatus();

            float[] vector = embedservice.getEmbedding(inventory.getProductName() + " " + remainingQty);

            // Native update
            inventoryRepo.updateWithVector(
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getStatus().name(),
                vector
            );

            if (inventory.getStatus() == InventoryStatus.OUT_OF_STOCK) {
                productRepository.findById(inventory.getProductId()).ifPresent(p -> {
                    p.setActive(false);
                    productRepository.save(p);
                });
            }
            responses.add(mapToResponse(inventory));
        }
        return responses;
    }

    private InventoryResponseAllDTO mapToResponseDTO(Inventory inventory) {
        InventoryResponseAllDTO dto = new InventoryResponseAllDTO();
        dto.setProductId(inventory.getProductId());
        dto.setProductName(inventory.getProductName());
        
        // 1. Set reorderLevel FIRST
        dto.setReorderLevel(inventory.getReorderLevel()); 
        
        // 2. Set quantity (this triggers updateStatus, which now has a reorderLevel)
        dto.setQuantity(inventory.getQuantity()); 
        
        dto.setStatus(inventory.getStatus());
        dto.setActive(inventory.getActive());
        return dto;
    }
   
	public List<InventoryResponseAllDTO> getAll() {
		// TODO Auto-generated method stub
		 return inventoryRepo.findAll()
	                .stream()
	                .map(this::mapToResponseDTO)
	                .collect(Collectors.toList());
	}

}