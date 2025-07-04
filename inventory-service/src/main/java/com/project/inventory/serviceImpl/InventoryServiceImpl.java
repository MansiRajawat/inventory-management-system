package com.project.inventory.serviceImpl;

import com.project.inventory.model.Details;
import com.project.inventory.model.FileResponse;
import com.project.inventory.model.ProductDetails;
import com.project.inventory.model.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface InventoryServiceImpl {
public List<ProductResponse> saveListOfProductDetails (Details details);
public FileResponse retriveListOfProducts(String type);
public Optional<ProductDetails> getProductById(Long id);
public Optional<ProductDetails> deleteProduct(Long id);
public ProductDetails updateProductDetails(Long id , ProductDetails details);
public Optional<ProductDetails> restoreProductDetails(Long id, int quantity);
public List<ProductDetails> getLimitedProductDetails(Integer limit, Integer offset);
}
