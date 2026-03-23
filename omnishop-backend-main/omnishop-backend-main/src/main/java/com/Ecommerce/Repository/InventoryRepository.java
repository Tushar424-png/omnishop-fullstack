package com.Ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Ecommerce.Entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);
    Optional<Inventory> findByProductIdAndActiveTrue(Long productId);
     
    @Query(value = "SELECT * FROM inventory ORDER BY embedding <=> CAST(:queryVector AS vector) LIMIT :limit", nativeQuery = true)
    List<Inventory> findSimilarInventory(@Param("queryVector") float[] queryVector, @Param("limit") int limit);
    
    // Naya update: Casting ke saath manual save
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO inventory (product_id, product_name, quantity, status, reorder_level, active, embedding) " +
                   "VALUES (:productId, :productName, :quantity, :status, :reorderLevel, :active, CAST(:embedding AS vector))",
           nativeQuery = true)
    void saveWithVector(
        @Param("productId") Long productId,
        @Param("productName") String productName,
        @Param("quantity") Integer quantity,
        @Param("status") String status,
        @Param("reorderLevel") Integer reorderLevel,
        @Param("active") Boolean active,
        @Param("embedding") float[] embedding
    );

    // Update ke liye alag method (Restock aur Reduce ke kaam aayega)
    @Modifying
    @Transactional
    @Query(value = "UPDATE inventory SET quantity = :quantity, status = :status, embedding = CAST(:embedding AS vector) " +
                   "WHERE product_id = :productId", nativeQuery = true)
    void updateWithVector(
        @Param("productId") Long productId, 
        @Param("quantity") Integer quantity, 
        @Param("status") String status, 
        @Param("embedding") float[] embedding
    );
	Inventory findByProductName(String name);
}