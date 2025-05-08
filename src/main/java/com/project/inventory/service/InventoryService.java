package com.project.inventory.service;

import com.project.inventory.dao.ProductRepository;
import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InventoryService implements InventoryServiceImpl {

    @Autowired
    private ProductRepository productDao;


    @Override
    public List<ProductDetails> saveListOfProductDetails(Details details) {
        if (details == null || details.getProductDetails() == null || details.getProductDetails().isEmpty()) {
            throw new IllegalArgumentException("Product details list cannot be null or empty");
        }
        List<ProductDetails> productDetailsList = details.getProductDetails().stream()
                .map(this::mapProductDetails)
                .collect(Collectors.toList());
        return productDao.saveAll(productDetailsList);
    }

    private ProductDetails mapProductDetails(ProductDetails productDetails) {
        ProductDetails mappedDetails = new ProductDetails();
        mappedDetails.setProductId(productDetails.getProductId());
        mappedDetails.setProductName(productDetails.getProductName());
        mappedDetails.setProductPrice(productDetails.getProductPrice());
        mappedDetails.setProductCount(productDetails.getProductCount());
        return mappedDetails;
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
