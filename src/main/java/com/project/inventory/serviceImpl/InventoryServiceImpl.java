package com.project.inventory.serviceImpl;

import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.model.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface InventoryServiceImpl {
public List<ProductResponse> saveListOfProductDetails (Details details);
public Details retriveListOfProducts();
public Optional<ProductDetails> getProductById(Long id);
public Optional<ProductDetails> deleteProduct(Long id);
public Details updateProductDetails(Details details);
}
