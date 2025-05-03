package com.project.inventory.controller;


import com.project.inventory.model.Details;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/inventory")
public class InventoryController {
    //we're going to use logger instead of system print statements

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    // /getAllProducts (GET)
    // /inventory (POST)
    // /inventoryById (PUT)
    // /deleteInventoryById (DELETE)
    // /getProductById (GraphQl)

    //response entity is a part of spring framework , which will help us to show the output of the response and
    // corresponding status http code.
    @PostMapping("/")
    public ResponseEntity<Details> SaveProductDetails(@RequestBody Details details){

        logger.info("inside the post method");

        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
