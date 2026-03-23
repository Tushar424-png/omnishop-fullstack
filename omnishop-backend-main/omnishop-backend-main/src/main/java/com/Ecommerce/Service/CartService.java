package com.Ecommerce.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Ecommerce.Entity.CartItem;
import com.Ecommerce.Entity.User;
import com.Ecommerce.Repository.CartRepository;
import com.Ecommerce.Repository.UserRepository;
import com.Ecommerce.dto.CartRequest;
import com.Ecommerce.dto.CartResponse;

@Service
public class CartService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public CartService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    public List<CartResponse> getall() {
        List<CartItem> CartItem = cartRepository.findAll();
        return CartItem.stream()
                .map(Item -> mapToCartResponse(Item))
                .toList();
    }

    public CartResponse addToCart(CartRequest cartRequest) {
        User user = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartItem item = new CartItem();
        item.setProductId(cartRequest.getProductId());
        item.setQuantity(cartRequest.getQuantity());
        item.setUser(user);
        CartItem res = cartRepository.save(item);
        return mapToCartResponse(res);
    }

    private CartResponse mapToCartResponse(CartItem res) {

        CartResponse response = new CartResponse();
        response.setId(res.getId());
        response.setProductId(res.getProductId());
        response.setUserId(res.getUser().getId());
        response.setQuantity(res.getQuantity());
        return response;
    }

    
    public void removeFromCart(Long userId, Long productId) {

        CartItem cart = cartRepository
                .findByUserIdAndProductId(userId, productId)
                .orElseThrow(() ->
                        new RuntimeException("Cart item not found"));

        cartRepository.delete(cart);
    }
}