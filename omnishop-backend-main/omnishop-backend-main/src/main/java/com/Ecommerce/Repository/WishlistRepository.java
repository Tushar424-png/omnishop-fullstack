package com.Ecommerce.Repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Ecommerce.Entity.User;
import com.Ecommerce.Entity.WishlistItem;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {

	Optional<WishlistItem> findByUserIdAndProductId(Long userId, Long productId);

}