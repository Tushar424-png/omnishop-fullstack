package com.Ecommerce.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.Ecommerce.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO orders (user_id, total_amount, order_date, delivery_date, order_status, address, embedding) " +
                   "VALUES (:#{#o.userId}, :#{#o.totalAmount}, :#{#o.orderDate}, :#{#o.deliveryDate}, " +
                   "CAST(:#{#o.orderStatus.name()} AS varchar), :#{#o.address}, :embedding)", 
           nativeQuery = true)
    void saveWithVector(@Param("o") Order o, @Param("embedding") float[] embedding);

    @Query(value = "SELECT * FROM orders o WHERE o.user_id = :userId " + 
                   "ORDER BY o.embedding <=> CAST(:queryVector AS vector) LIMIT :limit", 
           nativeQuery = true)
    List<Order> findSimilarOrders(@Param("userId") Long userId, 
                                  @Param("queryVector") float[] queryVector, 
                                  @Param("limit") int limit);

	List<Order> findByUserId(Long userId);
}