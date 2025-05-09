package com.project.inventory.serviceImpl;

import com.project.inventory.model.Details;
import com.project.inventory.model.ProductDetails;

import java.util.List;
import java.util.Optional;

public interface InventoryServiceImpl {
public List<ProductDetails> saveListOfProductDetails (Details details);
public Details retriveListOfProducts();
public Optional<Details> getProductById(Long id);
public Optional<Details> deleteProduct(Long id);
public Details updateProductDetails(Details details);
}
