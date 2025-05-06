package com.project.inventory.service;

import com.project.inventory.model.Details;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryService implements InventoryServiceImpl {

    @Override
    public List<Details> saveListOfProductDetails(Details details) {
        return List.of();
    }

    @Override
    public List<Details> retriveListOfProducts() {
        return List.of();
    }
}
