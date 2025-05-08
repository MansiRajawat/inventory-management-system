package com.project.inventory.controller;


import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/inventory")
public class InventoryController {
    //we're going to use logger instead of system print statements

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    @Autowired // if we use this annotation . it means we are creating the bean directly
    private InventoryService inventoryService;

    //response entity is a part of spring framework , which will help us to show the output of the response and
    // corresponding status http code.
    @PostMapping("/")
    public ResponseEntity<List<ProductDetails>> SaveProductDetails(@RequestBody Details details) {
        logger.info("inside the post method");
        List<ProductDetails> productDetailList = inventoryService.saveListOfProductDetails(details);
        return new ResponseEntity<>(productDetailList, HttpStatus.CREATED);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Details>> getListOfProducts() {
        List<Details> getProductsList = inventoryService.retriveListOfProducts();
        return new ResponseEntity<>(getProductsList, HttpStatus.OK);
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<Optional<Details>> getProductDetailsById(@PathVariable("id") Long id) {
        Optional<Details> productDetails = inventoryService.getProductById(id);
        return ResponseEntity.ok(productDetails);
    }

    @DeleteMapping("/deleteInventory/{id}")
    public ResponseEntity<String> deleteProductDetails(@PathVariable("id") Long id) {
        Optional<Details> retriveProductDetails = inventoryService.getProductById(id);
        if ( retriveProductDetails.isPresent() ) {
            inventoryService.deleteProduct(id);
            return ResponseEntity.ok("Product Deleted");
        }
        return new ResponseEntity<>("Product Not Present In Database", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/updateProductDetails")
    public ResponseEntity<Details> updateProductDetails(@RequestBody Details details) {
        Details updateDetails = inventoryService.updateProductDetails(details);
        return new ResponseEntity<Details>(updateDetails, HttpStatus.OK);
    }
}
