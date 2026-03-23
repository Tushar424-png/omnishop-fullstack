package com.Ecommerce.Service;


import java.util.List;


import org.springframework.stereotype.Service;

import com.Ecommerce.Entity.CartItem;
import com.Ecommerce.Entity.User;
import com.Ecommerce.Entity.WishlistItem;
import com.Ecommerce.Repository.UserRepository;
import com.Ecommerce.Repository.WishlistRepository;
import com.Ecommerce.dto.WishlistRequest;
import com.Ecommerce.dto.WishlistResponse;

@Service
public class WishlistService {
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistService(UserRepository userRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public WishlistResponse addToWishlist(WishlistRequest wishlistRequest) {
        User user = userRepository.findById(wishlistRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        WishlistItem item = new WishlistItem();
        item.setProductId(wishlistRequest.getProductId());

        item.setUser(user);
        WishlistItem res = wishlistRepository.save(item);
        return mapToWishlistResponse(res);


    }

    private WishlistResponse mapToWishlistResponse(WishlistItem res) {

        WishlistResponse response = new WishlistResponse();
        response.setId(res.getId());
        response.setProductId(res.getProductId());
        response.setUserId(res.getUser().getId());
        return response;
    }

    public List<WishlistResponse> getall() {
        List<WishlistItem> wishlistItem = wishlistRepository.findAll();
        return wishlistItem.stream()
                .map(Item -> mapToWishlistResponse(Item))
                .toList();
    }

	public void removeFromWishlist(Long userId, Long productId) {
		

		        WishlistItem Item = wishlistRepository
		                .findByUserIdAndProductId(userId, productId)
		                .orElseThrow(() ->
		                        new RuntimeException("Wishlist item not found"));

		        wishlistRepository.delete(Item);
		    }

}
