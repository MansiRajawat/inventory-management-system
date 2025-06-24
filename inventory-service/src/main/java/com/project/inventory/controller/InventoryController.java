package com.project.inventory.controller;

import com.project.inventory.model.*;
import com.project.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/inventory")
@Tag(name = "Inventory Controller", description = "Endpoints for managing inventory")
public class InventoryController {
    //we're going to use logger instead of system print statements

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    @Autowired // if we use this annotation . it means we are creating the bean directly
    private InventoryService inventoryService;

    //response entity is a part of spring framework , which will help us to show the output of the response and
    // corresponding status http code.

    @Operation(summary = "Create new product", description = "Adds a new product to the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<InventoryResponse> SaveProductDetails(@RequestBody Details details) {
        logger.info("inside the post method");
        List<ProductResponse> productDetailList = inventoryService.saveListOfProductDetails(details);
        InventoryResponse response = new InventoryResponse();
        response.setProductResponses(productDetailList);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<byte[]> getListOfProducts(@RequestParam(required = false) String type) {
        FileResponse response = inventoryService.retriveListOfProducts(type);

        HttpHeaders headers = new HttpHeaders();
        if (response.getFileName() != null) {
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + response.getFileName());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(response.getMediaType())
                .body(response.getData());
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<Optional<ProductDetails>> getProductDetailsById(@PathVariable("id") Long productId) {
        Optional<ProductDetails> productDetails = inventoryService.getProductById(productId);
        return ResponseEntity.ok(productDetails);
    }

    @DeleteMapping("/deleteInventory/{id}")
    public ResponseEntity<String> deleteProductDetails(@PathVariable("id") Long id) {

           Optional<ProductDetails> deleteProduct = inventoryService.deleteProduct(id);
           if (deleteProduct.isPresent()) {
            return ResponseEntity.ok("Product Deleted");
        }
        return new ResponseEntity<>("Product Not Present In Database", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/updateProductDetails/{id}")
    public ResponseEntity<ProductDetails> updateProductDetails(@PathVariable Long id , @RequestBody ProductDetails details) {
        ProductDetails updateDetails = inventoryService.updateProductDetails(id , details);
        return new ResponseEntity<>(updateDetails, HttpStatus.OK);
    }

    @PutMapping("/restoreProductCount/{id}")
    public ResponseEntity<String> restoreProductCount(@PathVariable Long id, @RequestParam int quantity) {
        Optional<ProductDetails> restoreProduct =  inventoryService.restoreProductDetails(id, quantity);
        if (restoreProduct.isPresent()) {

            return ResponseEntity.ok("Inventory restored");
        }
        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/limitProductData")
    public ResponseEntity<List<ProductDetails>> getLimitedProductDetails(@RequestParam(value = "limit", required = false) Integer limit , @RequestParam(value = "page", required = false) Integer page) {

        List<ProductDetails> getProductInfo = inventoryService.getLimitedProductDetails(Integer.valueOf(limit), Integer.valueOf(page));
        return new ResponseEntity<>(getProductInfo, HttpStatus.OK);
    }


}
