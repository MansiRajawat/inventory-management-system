package com.project.inventory.controller;


import com.project.inventory.model.Details;
import com.project.inventory.service.InventoryService;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/inventory")
public class InventoryController {
    //we're going to use logger instead of system print statements

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    @Autowired // if we use this annotation . it means we are creating the bean directly
    private InventoryService inventoryService;

    // /getAllProducts (GET)
    // /inventory (POST)
    // /inventoryById (PUT)
    // /deleteInventoryById (DELETE)
    // /getProductById (GraphQl)

    //response entity is a part of spring framework , which will help us to show the output of the response and
    // corresponding status http code.
    @PostMapping("/")
    public ResponseEntity<List<Details>>SaveProductDetails(@RequestBody Details details){

        logger.info("inside the post method");
        List<Details> productDetailList = inventoryService.saveListOfProductDetails(details);
        return new ResponseEntity<>(productDetailList, HttpStatus.CREATED);


    }
    @GetMapping("/getAllProducts")
    public  ResponseEntity<List<Details>> getListOfProducts(){
        List<Details> getProductsList = inventoryService.retriveListOfProducts();
        return new ResponseEntity<>(getProductsList, HttpStatus.OK);
    }
}
