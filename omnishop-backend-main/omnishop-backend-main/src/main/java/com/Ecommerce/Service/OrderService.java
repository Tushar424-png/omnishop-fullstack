package com.Ecommerce.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.Ecommerce.Exception.ApiResponse;
import com.Ecommerce.Repository.OrderRepository;
import com.Ecommerce.dto.*;
import com.Ecommerce.Entity.Order;
import com.Ecommerce.Entity.OrderItem;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userservice;
    private final ProductService productservice;
    private final Inventoryservice inventoryservice;
    private final AIEmbeddingService embedservice;

    public OrderService(OrderRepository orderRepository, UserService userservice, ProductService productservice, Inventoryservice inventoryservice,AIEmbeddingService embedservice) {
        this.orderRepository = orderRepository;
        this.userservice = userservice;
        this.productservice = productservice;
        this.inventoryservice = inventoryservice;
        this.embedservice=embedservice;
    }

    // ---------------- CHECK INVENTORY (NO CACHE) ----------------
    public ApiResponse<List<OrderItemErrorDTO>> checkInventory(Long userId) {
        // ❌ Redis not used (real-time)
        UserResponse user = userservice.getuser(userId);
        List<CartResponse> cartItems = user.getCartItems();

        List<InventoryCheckRequestDTO> checkRequests =
                cartItems.stream()
                        .map(c -> {
                            InventoryCheckRequestDTO dto = new InventoryCheckRequestDTO();
                            dto.setProductId(c.getProductId());
                            dto.setRequiredQuantity(c.getQuantity());
                            return dto;
                        }).toList();

        List<InventoryCheckResponseDTO> responses =
                inventoryservice.checkStock(checkRequests);

        List<OrderItemErrorDTO> errors = responses.stream()
                .filter(r -> !r.getAvailable())
                .map(r -> {
                    OrderItemErrorDTO err = new OrderItemErrorDTO();
                    err.setProductId(r.getProductId());
                    err.setReason(r.getMessage());
                    err.setAvailableQuantity(r.getAvailableQuantity());
                    return err;
                }).toList();

        ApiResponse<List<OrderItemErrorDTO>> response = new ApiResponse<>();

        if (!errors.isEmpty()) {
            response.setSuccess(false);
            response.setStatus("STOCK_FAILED");
            response.setMessage("Some items are out of stock");
            response.setData(errors);
            return response;
        }

        response.setSuccess(true);
        response.setStatus("STOCK_OK");
        response.setMessage("All items available");
        response.setData(List.of());
        return response;
    }

    // ---------------- CREATE ORDER (NO CACHE) ----------------
    public ApiResponse<OrderResponse> createOrder(Long userId) {

        UserResponse user = userservice.getuser(userId);
        List<CartResponse> cartItems = user.getCartItems();

        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(user.getAddress());
        order.setOrderDate(LocalDate.now());
        order.setDeliveryDate(LocalDate.now().plusDays(2));
        order.updateStatusBasedOnDate();

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartResponse cart : cartItems) {
            ProductResponse product = productservice.getOne(cart.getProductId());

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setOrder(order);

            orderItems.add(item);
            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount.doubleValue());

        try {
            // 1️⃣ Embedding text banao (Thoda detail aur add kar do accuracy ke liye)
            String textForAi = String.format("Order for User %d, Amount %.2f, Status %s", 
                                              userId, order.getTotalAmount(), order.getOrderStatus());
            
            float[] vector = embedservice.getEmbedding(textForAi);
            
            if (vector != null) {
                // ✅ Galti Yahan Thi: String vectorString banane ki zaroorat nahi hai!
                // Direct float[] vector pass karo
                orderRepository.saveWithVector(order, vector); 
                
                // 2️⃣ Response ke liye naya data fetch karo
                // Note: saveWithVector mein product_id ya id handle ho raha hai toh hi ye chalega
                order = orderRepository.findById(order.getId()).orElse(order);
            } else {
                // Fallback: AI fail ho gaya toh normal save
                order = orderRepository.save(order);
            }
        } catch (Exception e) {
            System.err.println("AI Embedding failed: " + e.getMessage());
            // Error aane par normal save taaki customer ka order stop na ho
            order = orderRepository.save(order);
        }

        // Reduce inventory
        List<InventoryReduceRequestDTO> reduceRequests =
                cartItems.stream()
                         .map(c -> new InventoryReduceRequestDTO(c.getProductId(), c.getQuantity()))
                         .toList();
        inventoryservice.reduceInventoryBulk(reduceRequests);

        // Clear cart
        userservice.deleteCartItem(userId);

        // Prepare response
        ApiResponse<OrderResponse> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setStatus("ORDER_CREATED");
        response.setMessage("Order placed successfully");
        response.setData(mapToOrderResponse(order));

        return response;
    }

    // ---------------- GET ALL ORDERS (REDIS) ----------------
    public List<OrderResponse> get() {
        List<OrderResponse> response = orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return response;
    }

    // ---------------- MAPPER ----------------
    private OrderResponse mapToOrderResponse(Order order) {

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getId());
        orderResponse.setUserId(order.getUserId());
        orderResponse.setAddress(order.getAddress());
        orderResponse.setOrderDate(order.getOrderDate());
        orderResponse.setDeliveryDate(order.getDeliveryDate());
        orderResponse.setStatus(order.getOrderStatus());
        orderResponse.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> itemResponses =
                order.getOrderItems() == null
                        ? new ArrayList<>()
                        : order.getOrderItems().stream()
                        .map(item -> {
                            OrderItemResponse dto = new OrderItemResponse();
                            dto.setId(item.getId());
                            dto.setProductId(item.getProductId());
                            dto.setProductName(item.getProductName());
                            dto.setPrice(item.getPrice());
                            dto.setQuantity(item.getQuantity());
                            return dto;
                        }).collect(Collectors.toList());

        orderResponse.setOrderItem(itemResponses);
        return orderResponse;
    }
}