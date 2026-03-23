package com.Ecommerce.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Ecommerce.Entity.Products;
import com.Ecommerce.Repository.ProductRepository;
import com.Ecommerce.dto.InventoryCreateRequestDTO;
import com.Ecommerce.dto.ProductRequest;
import com.Ecommerce.dto.ProductResponse;

//Change your import to this:
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    // InventoryService ko inject karein
    private final Inventoryservice inventoryService;
    private final ProductRepository productrepository;
    private final CloudinaryService cloudinaryservice;
    private final AIEmbeddingService aiEmbeddingService;
    private final GeminiService geminiService;
    private final PushNotificationService pushNotificationService;

    public ProductService(ProductRepository productrepository, 
                          CloudinaryService cloudinaryservice, 
                          AIEmbeddingService aiEmbeddingService,
                          Inventoryservice inventoryService,
                          GeminiService geminiService,
                          PushNotificationService pushNotificationService) {
        this.productrepository = productrepository;
        this.cloudinaryservice = cloudinaryservice;
        this.aiEmbeddingService = aiEmbeddingService;
        this.inventoryService = inventoryService;
        this.geminiService=geminiService;
        this.pushNotificationService=pushNotificationService;
    }

    @Transactional
    public ProductResponse add(ProductRequest productRequest, MultipartFile image) throws IOException {

        if (productrepository.existsByName(productRequest.getName())) {
            throw new RuntimeException("Product with name already exists");
        }

        // 1. Image upload
        String imageUrl = cloudinaryservice.uploadImage(image);

        // 2. Get embedding
        float[] vector = aiEmbeddingService.getEmbedding(productRequest.getName() + " " + productRequest.getDescription());

        if (vector == null || vector.length == 0) {
            throw new RuntimeException("AI Embedding fail ho gayi!");
        }

        // 3. Save product using Native Query (Explicit Casting)
        productrepository.saveProductWithVector(
            productRequest.getName(),
            productRequest.getDescription(),
            productRequest.getPrice(),
            productRequest.getActive(),
            productRequest.getCategory(),
            imageUrl,
            vector
        );
        String aiMessage = geminiService.askGroq(
        	    "You are an ecommerce marketing expert.",
        	    "Write a short exciting push notification for a new product named "
        	    + productRequest.getName() +
        	    " priced at ₹" + productRequest.getPrice()
        	);

        	pushNotificationService.sendNewProductNotification(
        	    "🔥 New Arrival!",
        	    aiMessage
        	);

        // 4. Fetch the saved product to get the ID for Inventory
        // Native query ID return nahi karti, isliye humein wapas fetch karna padega
        Products savedProduct = productrepository.findByName(productRequest.getName())
                .orElseThrow(() -> new RuntimeException("Product save hone ke baad nahi mila!"));

        // 5. AUTOMATIC INVENTORY CREATION
        InventoryCreateRequestDTO inventoryRequest = new InventoryCreateRequestDTO();
        inventoryRequest.setProductId(savedProduct.getId());
        inventoryRequest.setProductName(savedProduct.getName());
        inventoryRequest.setQuantity(10); 
        inventoryRequest.setActive(true);

        inventoryService.addNewProductInInventory(inventoryRequest);

        return mapToResponse(savedProduct);
    }
    // ---------------- GET ALL PRODUCTS (REDIS) ----------------
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productrepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- GET ONE PRODUCT (REDIS) ----------------
    public ProductResponse getOne(Long id) {
        Products product = productrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductResponse response = mapToResponse(product);
        return response;
    }

    public void deactivate(Long productId) {

        Products product = productrepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setActive(false);
        productrepository.save(product);
    }

    // ---------------- MAPPER ----------------
    private ProductResponse mapToResponse(Products product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setPrice(product.getPrice());
        response.setActive(product.getActive());
        return response;
    }
}