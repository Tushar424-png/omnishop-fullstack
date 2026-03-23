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
import com.Ecommerce.Entity.Products;

        @Repository
        public interface ProductRepository extends JpaRepository<Products, Long> {

            boolean existsByName(String name);
            
            @Query(value = "SELECT id, name, description, price, active, category, image_url, NULL as embedding " +
                    "FROM products WHERE name = :name", nativeQuery = true)
            Optional<Products> findByName(@Param("name") String name);

            @Query(value = "SELECT * FROM products p ORDER BY p.embedding <=> CAST(:queryVector AS vector) LIMIT :limit", nativeQuery = true)
            List<Products> findSimilarProducts(@Param("queryVector") float[] queryVector, @Param("limit") int limit);

 
            @Modifying
            @Transactional
            @Query(value = "INSERT INTO products (name, description, price, active, category, image_url, embedding) " +
                           "VALUES (:name, :description, :price, :active, :category, :imageUrl, CAST(:embedding AS vector))", 
                   nativeQuery = true)
            void saveProductWithVector(
                @Param("name") String name, 
                @Param("description") String description, 
                @Param("price") java.math.BigDecimal price, 
                @Param("active") Boolean active, 
                @Param("category") String category, 
                @Param("imageUrl") String imageUrl, 
                @Param("embedding") float[] embedding
            );
            
        }


