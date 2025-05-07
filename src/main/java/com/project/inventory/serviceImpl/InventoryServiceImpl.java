package com.project.inventory.serviceImpl;

import com.project.inventory.model.Details;

import java.util.List;
import java.util.Optional;

public interface InventoryServiceImpl {
public List<Details> saveListOfProductDetails (Details details);
public List<Details> retriveListOfProducts();
public Optional<Details> getProductById(Long id);
public Optional<Details> deleteProduct(Long id);
public Details updateProductDetails(Details details);
}
