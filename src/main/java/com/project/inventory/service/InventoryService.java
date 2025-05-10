package com.project.inventory.service;

import com.project.inventory.dao.ProductRepository;
import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.serviceImpl.InventoryServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InventoryService implements InventoryServiceImpl {

    @Autowired
    private ProductRepository productDao;


    @Override
    public List<ProductDetails> saveListOfProductDetails(Details details) {
        if (CollectionUtils.isNotEmpty(details.getProductDetails()) ) {
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
    public Details retriveListOfProducts() {

        /* commenting this code for functional coding approach */
        //   List<ProductDetails> productDetails =  productDao.findAll();

       // List<Details> detailsList = new ArrayList<>()
        // if(CollectionUtils.isNotEmpty(productDetails)) {
//            Details details = new Details();
//            details.setProductDetails(productDetails);
//            return details;
//        }
 //       return new Details();

        return  Optional.of(productDao.findAll()).filter(list -> !list.isEmpty())
                .map(list -> {
                    var details = new Details();
                details.setProductDetails(list);
                return details; }).orElseGet(Details::new);
    }

    @Override
    public Optional<ProductDetails> getProductById(Long productId) {
        return productDao.findById(productId);
    }

    @Override
    public Optional<ProductDetails> deleteProduct(Long id) {
        Optional<ProductDetails> productOpt = getProductById(id);
        productOpt.ifPresent(productDao::delete);
        return productOpt;

    }

    @Override
    public Details updateProductDetails(Details details) {
        return null;
    }

}

