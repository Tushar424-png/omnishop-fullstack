package com.Ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce.Entity.CartItem;
import com.Ecommerce.Entity.User;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

	Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

}