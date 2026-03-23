package com.Ecommerce.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Ecommerce.Entity.Inventory;
import com.Ecommerce.Entity.InventoryStatus;
import com.Ecommerce.Entity.Order;
import com.Ecommerce.Entity.Products;
import com.Ecommerce.Repository.InventoryRepository;
import com.Ecommerce.Repository.OrderRepository;
import com.Ecommerce.Repository.ProductRepository;

@Service
public class ChatbotService {

    private final ProductRepository productRepository;
    private final AIEmbeddingService aiEmbeddingService;
    private final GeminiService geminiService;
    private final InventoryRepository inventoryRepo;
    private final OrderRepository orderRepo;

    public ChatbotService(ProductRepository productRepository,
                          AIEmbeddingService aiEmbeddingService,
                          GeminiService geminiService,
                          InventoryRepository inventoryRepo,
                          OrderRepository orderRepo) {
        this.productRepository = productRepository;
        this.aiEmbeddingService = aiEmbeddingService;
        this.geminiService = geminiService;
        this.inventoryRepo = inventoryRepo;
        this.orderRepo = orderRepo;
    }

    // ================= MAIN ENTRY =================

    public String handleUserQuery(String prompt) {

        String intent = detectIntent(prompt);

        if ("UNKNOWN".equals(intent)) {
            String llmResponse = getLLMDecisionOrReply(prompt);

            if (isIntent(llmResponse)) {
                intent = llmResponse.toUpperCase().trim();
            } else {
                return llmResponse;
            }
        }

        return switch (intent) {
            case "PRODUCT" -> handleProductQuery(prompt);
            case "ORDER" -> handleOrderQuery(prompt);
            case "INVENTORY" -> handleInventoryQuery(prompt);
            default -> geminiService.askGroq(
                    "You are an ecommerce support chatbot. Reply politely in Hinglish.",
                    prompt
            );
        };
    }

    // ================= INTENT =================

    private boolean isIntent(String response) {
        String r = response.toUpperCase().trim();
        return r.equals("PRODUCT") || r.equals("ORDER") || r.equals("INVENTORY");
    }

    private String getLLMDecisionOrReply(String prompt) {
        String systemPrompt = """
                You are an intelligent intent classifier.

                Reply ONLY with:
                PRODUCT
                ORDER
                INVENTORY

                If greeting or general talk, reply normally in Hinglish.
                """;

        return geminiService.askGroq(systemPrompt, prompt);
    }

    private String detectIntent(String prompt) {
        String p = prompt.toLowerCase();

        if (p.contains("order") || p.contains("delivery")
                || p.contains("tracking") || p.contains("refund")
                || p.contains("status"))
            return "ORDER";

        if (p.contains("stock") || p.contains("available")
                || p.contains("quantity"))
            return "INVENTORY";

        if (p.contains("product") || p.contains("price")
                || p.contains("buy") || p.contains("show")
                || p.contains("search"))
            return "PRODUCT";

        return "UNKNOWN";
    }

    // ================= PRODUCT =================

    private String handleProductQuery(String prompt) {

        try {

            float[] queryVector = aiEmbeddingService.getEmbedding(prompt);

            if (queryVector == null)
                return "AI service busy hai, thoda baad try karein.";

            List<Products> similar =
                    productRepository.findSimilarProducts(queryVector, 5);

            if (similar.isEmpty())
                return "Bhai, filhal aisa product nahi mila.";

            StringBuilder contextBuilder = new StringBuilder();

            for (Products p : similar) {

                Inventory inv =
                        inventoryRepo.findByProductName(p.getName());

                String stockStatus = "Unknown";

                if (inv != null) {

                	if (inv.getQuantity() <= 0 ||
                		    inv.getStatus() == InventoryStatus.OUT_OF_STOCK) {

                		    stockStatus = "Out of Stock ❌";

                		} else {

                		    stockStatus = "In Stock ✅ (Qty: " + inv.getQuantity() + ")";
                		}
                }

                contextBuilder.append(
                        String.format(
                                "Name:%s | Price:%s | Desc:%s | Stock:%s\n",
                                p.getName(),
                                p.getPrice(),
                                p.getDescription(),
                                stockStatus
                        )
                );
            }

            String systemPrompt = """
                    You are a professional ecommerce product assistant.

                    Use ONLY the provided product data.
                    Clearly mention stock availability.
                    If product is out of stock, inform politely.
                    Reply in friendly Hinglish.
                    Keep answer short (3-5 lines).
                    Do NOT invent products.
                    """;

            return geminiService.askGroq(
                    systemPrompt + "\n\nProduct Data:\n"
                            + contextBuilder.toString(),
                    prompt
            );

        } catch (Exception e) {
            return "Product dhoondne mein technical issue aa gaya.";
        }
    }

    // ================= INVENTORY =================

    private String handleInventoryQuery(String prompt) {

        try {

            float[] queryVector = aiEmbeddingService.getEmbedding(prompt);

            List<Inventory> similar =
                    inventoryRepo.findSimilarInventory(queryVector, 5);

            if (similar.isEmpty())
                return "Is product ka stock data nahi mila.";

            String context = similar.stream()
                    .map(i -> i.getProductName()
                            + " | Qty: " + i.getQuantity()
                            + " | Status: " + i.getStatus())
                    .collect(Collectors.joining("\n"));

            String systemPrompt = """
                    You are an inventory support assistant.

                    Use ONLY given stock data.
                    Clearly mention quantity and availability.
                    Reply in Hinglish.
                    Keep answer short.
                    """;

            return geminiService.askGroq(
                    systemPrompt + "\n\nInventory Data:\n" + context,
                    prompt
            );

        } catch (Exception e) {
            return "Inventory check karne mein issue aa gaya.";
        }
    }

    // ================= ORDER =================

    private String handleOrderQuery(String prompt) {

        try {

            Long orderId = extractOrderId(prompt);
            Long userId = extractUserId(prompt);

            if (orderId == null)
                return "Please order ID mention karein (e.g., Order 6 ka status).";

            Optional<Order> orderOpt =
                    orderRepo.findById(orderId);

            if (orderOpt.isEmpty())
                return "Order ID " + orderId + " nahi mila.";

            Order order = orderOpt.get();

            if (userId != null &&
                    !order.getUserId().equals(userId)) {

                return "Yeh order is user ka nahi hai.";
            }

            return "Order ID " + order.getId()
                    + " ka status hai: "
                    + order.getOrderStatus()
                    + ". Total amount: ₹"
                    + order.getTotalAmount();

        } catch (Exception e) {
            return "Order fetch karne mein error aa gaya.";
        }
    }

    // ================= ID EXTRACTORS =================

    private Long extractUserId(String prompt) {

        String lower = prompt.toLowerCase();

        java.util.regex.Pattern p =
                java.util.regex.Pattern.compile(
                        "user\\s*id\\s*(\\d+)"
                );

        java.util.regex.Matcher m =
                p.matcher(lower);

        if (m.find())
            return Long.parseLong(m.group(1));

        return null;
    }

    private Long extractOrderId(String prompt) {

        String lower = prompt.toLowerCase();

        java.util.regex.Pattern p =
                java.util.regex.Pattern.compile(
                        "order\\s*(?:id)?\\s*(\\d+)"
                );

        java.util.regex.Matcher m =
                p.matcher(lower);

        if (m.find())
            return Long.parseLong(m.group(1));

        return null;
    }
}