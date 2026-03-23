package com.Ecommerce.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce.Service.WishlistService;
import com.Ecommerce.dto.WishlistRequest;
import com.Ecommerce.dto.WishlistResponse;

@RequestMapping("/wishlist")
@RestController
public class WishlistController {
    private final WishlistService wishlistservice;

    public WishlistController(WishlistService wishlistservice) {
        this.wishlistservice = wishlistservice;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistResponse add(@RequestBody WishlistRequest wishlistRequest) {
        return wishlistservice.addToWishlist(wishlistRequest);
    }

    @GetMapping("/getall")
    @ResponseStatus(HttpStatus.OK)
    public List<WishlistResponse> getAll() {
        return wishlistservice.getall();
    }
    @DeleteMapping("/remove/{userId}/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void remove(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        wishlistservice.removeFromWishlist(userId, productId);
    }
}