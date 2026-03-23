package com.Ecommerce.Controller;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Ecommerce.Service.Inventoryservice;
import com.Ecommerce.dto.InventoryCheckRequestDTO;
import com.Ecommerce.dto.InventoryCheckResponseDTO;
import com.Ecommerce.dto.InventoryCreateRequestDTO;
import com.Ecommerce.dto.InventoryReduceRequestDTO;
import com.Ecommerce.dto.InventoryResponseAllDTO;
import com.Ecommerce.dto.InventoryResponseDTO;
import com.Ecommerce.dto.InventoryRestockRequestDTO;

@RestController
@RequestMapping("/Inventory")
public class InventoryController {

    private final Inventoryservice inventoryservice;

    InventoryController(Inventoryservice inventoryservice) {
        this.inventoryservice = inventoryservice;
    }
    
    @GetMapping("/getall")
    public List<InventoryResponseAllDTO> getall() {
    	return inventoryservice.getAll();
    }
    
    @PostMapping("/add")
    public InventoryResponseDTO add(@RequestBody InventoryCreateRequestDTO request) {
        System.out.println("Inventory Request = " + request);
        return inventoryservice.addNewProductInInventory(request);
    }

    @PutMapping("/ReduceQuantity")
    public List<InventoryResponseDTO> reduceQuantity(
            @RequestBody List<InventoryReduceRequestDTO> requests) {
        return inventoryservice.reduceInventoryBulk(requests);
    }

    @PostMapping("/check")
    public List<InventoryCheckResponseDTO> checkStock(
            @RequestBody List<InventoryCheckRequestDTO> requestList) {
        return inventoryservice.checkStock(requestList);
    }

    @PutMapping("/Restock")
    public InventoryResponseDTO restock(@RequestBody InventoryRestockRequestDTO request) {
        return inventoryservice.restockInventory(request);
    }

}