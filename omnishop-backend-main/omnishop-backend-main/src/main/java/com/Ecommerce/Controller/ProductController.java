package com.Ecommerce.Controller;

import org.springframework.http.MediaType;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.Ecommerce.Service.ProductService;
import com.Ecommerce.dto.ProductRequest;
import com.Ecommerce.dto.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

@RequestMapping("/products")
@RestController
public class ProductController {
    private final ProductService productservice;
    private final ObjectMapper objectMapper;

    public ProductController(ProductService productservice, ObjectMapper objectMapper) {
        this.productservice = productservice;
        this.objectMapper = objectMapper;
    }

    @PostMapping(
            value = "/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> addProduct(
            @RequestParam("product") String productJson,
            @RequestParam("image") MultipartFile image
    ) throws IOException, java.io.IOException {

        // JSON string → DTO
        ProductRequest productRequest =
                objectMapper.readValue(productJson, ProductRequest.class);

        ProductResponse response =
                productservice.add(productRequest, image);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/getall")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getall() {
        return productservice.getAll();

    }

    @GetMapping("/getone/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProductById(@PathVariable Long id) {
        return productservice.getOne(id);

    }

}