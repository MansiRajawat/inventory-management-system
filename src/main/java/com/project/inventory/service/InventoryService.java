package com.project.inventory.service;

import com.project.inventory.model.Details;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Details> getProductById(Long id) {
        return null;
    }

    @Override
    public Optional<Details> deleteProduct(Long id) {
        return Optional.empty();
    }

    @Override
    public Details updateProductDetails(Details details) {
        return null;
    }
}
