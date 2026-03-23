package com.Ecommerce.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.Ecommerce.Repository.UserRepository;
import com.Ecommerce.Entity.User;
import com.Ecommerce.dto.UserResponse;
import com.Ecommerce.dto.*;
import com.Ecommerce.dto.UserRequest;

import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse save(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setFullName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        user.setPassword(encodedPassword);
        user.setRole("USER");
        user.setAddress(userRequest.getAddress());
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setPassword(user.getPassword());
        response.setAddress(user.getAddress());
        // Mapping Cart Items (Entity to DTO)
        if (user.getCartItems() != null) {
            List<CartResponse> cartDtos = user.getCartItems().stream().map(item -> {
                CartResponse dto = new CartResponse();
                dto.setId(item.getId());
                dto.setProductId(item.getProductId());
                dto.setQuantity(item.getQuantity());
                dto.setUserId(user.getId()); // Sirf ID, object nahi
                return dto;
            }).collect(Collectors.toList());
            response.setCartItems(cartDtos);
        }

        // Mapping Wishlist Items (Entity to DTO)
        if (user.getWishlistItems() != null) {
            List<WishlistResponse> wishlistDtos = user.getWishlistItems().stream().map(item -> {
                WishlistResponse dto = new WishlistResponse();
                dto.setId(item.getId());
                dto.setProductId(item.getProductId());
                dto.setUserId(user.getId()); // Sirf ID
                return dto;
            }).collect(Collectors.toList());
            response.setWishlistItems(wishlistDtos);
        }

        return response;
    }

    public boolean validateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        // ✅ Use BCrypt matches
        return passwordEncoder.matches(password, user.getPassword());
    }

    public String getRoleByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public UserResponse getuser(Long id) {
        // TODO Auto-generated method stub
        User save = userRepository.getById(id);
        return mapToUserResponse(save);

    }

    public void deleteCartItem(Long UserId) {
        User user = userRepository.findById(UserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // List ko null set karne ke bajaye uske items clear karein
        if (user.getCartItems() != null) {
            user.getCartItems().clear(); 
        }
        
        userRepository.save(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToUserResponse(user);
    }

}